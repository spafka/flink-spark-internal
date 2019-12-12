package io.github.spafka;


import kafka.server.KafkaServerStartable;
import org.I0Itec.zkclient.ZkServer;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.junit.Test;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class KafkaStandaloneServer {

    static final String TOPIC = "test";
    static final String CG="spafka";

    public static void main(String[] args) throws IOException, QuorumPeerConfig.ConfigException, InterruptedException {


        new Thread(() -> {
            try {
                FileUtils.deleteDirectory(new File("/tmp/zookeeper"));

                QuorumPeerConfig config = new QuorumPeerConfig();
                InputStream is = ZkServer.class.getResourceAsStream("/zookeeper.properties");
                Properties p = new Properties();
                p.load(is);
                config.parseProperties(p);
                ServerConfig serverconfig = new ServerConfig();
                serverconfig.readFrom(config);
                new ZooKeeperServerMain().runFromConfig(serverconfig);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (QuorumPeerConfig.ConfigException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {

            try {
                FileUtils.deleteDirectory(new File("/tmp/kafka/data"));


                InputStream is = KafkaStandaloneServer.class.getResourceAsStream("/server.properties");
                Properties p = new Properties();
                p.load(is);
                is.close();
                KafkaServerStartable kafkaServerStartable = KafkaServerStartable.fromProps(p);
                kafkaServerStartable.startup();
                kafkaServerStartable.awaitShutdown();
            } catch (IOException e) {

            }


        }).start();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer kafkaProducer = new KafkaProducer(props);

        TimeUnit.SECONDS.sleep(10);
        while (true) {

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            kafkaProducer.send(new ProducerRecord("flink", null, line));

        }

    }


    @Test
    public void createTopic() throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);

        AdminClient adminClient = AdminClient.create(props);

        ListTopicsResult listTopicsResult = adminClient.listTopics();

        System.out.println(listTopicsResult.listings().get());

        NewTopic test = new NewTopic(TOPIC, 3, (short) 1);
        adminClient.createTopics(Arrays.asList(test));

        ListTopicsResult nowtopics = adminClient.listTopics();

        System.out.println(nowtopics.listings().get());

    }

    @Test
    public void producer() throws ExecutionException, InterruptedException {


        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);


        RecordMetadata van = producer.send(new ProducerRecord<String, String>(TOPIC, "", "van")).get();

        System.out.println(van);


    }


    @Test
    public void consumer() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CG);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(TOPIC));

        while (true) {


            ConsumerRecords<String, String> polled = consumer.poll(100L);

            Iterator<ConsumerRecord<String, String>> iterator = polled.iterator();

            iterator.forEachRemaining(x->{

                System.out.println(x);
            });


        }


    }

    @Test
    public void seek() throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);

        AdminClient adminClient = AdminClient.create(props);

        ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(CG);


        Map<TopicPartition, OffsetAndMetadata> tos = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();


        tos.forEach((k,v)->{
            System.out.println(new Tuple2<>(k,v));
        });

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CG);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(TOPIC));


        Set<TopicPartition> topicPartitions = tos.keySet();

        topicPartitions.forEach(x->{

            // 没有提交过偏移量的会之间报错
            consumer.seek(x,0L);
        });


        while (true){
            ConsumerRecords<String, String> consumerRecords = consumer.poll(100L);
            consumerRecords.iterator().forEachRemaining(x->{
                System.out.println(x);
            });
        }

    }



    @Test
    public void seek2End() throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CG);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
        long start = System.currentTimeMillis();
        Set<TopicPartition> assignment = new HashSet<>();
        while (assignment.size() == 0) {
            consumer.poll(Duration.ofMillis(100));
            assignment = consumer.assignment();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println(assignment);
        for (TopicPartition tp : assignment) {
            consumer.seek(tp, 10);
        }
        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(1000));
            //consume the record.
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset() + ":" + record.value());
            }
        }

    }
}