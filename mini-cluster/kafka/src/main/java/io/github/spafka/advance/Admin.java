package io.github.spafka.advance;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Admin {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        AdminClient adminClient = AdminClient.create(props);


        ListTopicsResult listTopicsResult = adminClient.listTopics();

        System.out.println(listTopicsResult.listings().get());



        adminClient.createPartitions(new HashMap<String, NewPartitions>(){{put("test", NewPartitions.increaseTo(2));}});

        DescribeTopicsResult test = adminClient.describeTopics(Arrays.asList("test"));


        test.all().get().forEach((k,v)->{
            System.out.println(k+v);
        });
        System.out.println();

        ListConsumerGroupOffsetsResult test1 = adminClient.listConsumerGroupOffsets("spafka");

        // offset of group
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = test1.partitionsToOffsetAndMetadata().get();

        topicPartitionOffsetAndMetadataMap.forEach((k,v)->{
            System.out.println(k+"  "+v);
        });

    }
}
