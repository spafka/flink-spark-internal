package io.github.spafka.advance;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class 幂等 {

    public static final String brokerList = "localhost:9092";
    public static final String topic = "test";
    public static final String groupId = "cg";

    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return props;
    }

    /***
     * 开启幂等性之后producer会更具producerId + seq
     * broker 根据提交的producerId+seq判断是否是刚好的producerId +（seq+1） ，直到producer发送成功，这就保证了发送的幂等性
     * @throws InterruptedException
     */
    @Test
    public void p1() throws InterruptedException {


        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");


        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        while (true){
            producer.send(new ProducerRecord<>(topic,"van"));

            TimeUnit.MICROSECONDS.sleep(1);
        }



    }

    @Test
    public void p2() throws InterruptedException {


        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");


        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        while (true){
            producer.send(new ProducerRecord<>(topic,"van"));

            TimeUnit.MICROSECONDS.sleep(1);

        }



    }
}
