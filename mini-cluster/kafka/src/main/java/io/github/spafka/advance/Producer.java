/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.spafka.advance;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


@Slf4j
public class Producer extends Thread {
    private final KafkaProducer<Integer, String> producer;
    private final String topic;


    public Producer(String topic) {

        // At least once
//        Kafka到底会不会丢数据(data loss)? 通常不会，但有些情况下的确有可能会发生。下面的参数配置及Best practice列表可以较好地保证数据的持久性(当然是trade-off，牺牲了吞吐量)。笔者会在该列表之后对列表中的每一项进行讨论，有兴趣的同学可以看下后面的分析。
//
//        block.on.buffer.full = true
//        acks = all
//        retries = MAX_VALUE
//        max.in.flight.requests.per.connection = 1
//        使用KafkaProducer.send(record, callback)
//        callback逻辑中显式关闭producer：close(0)
//        unclean.leader.election.enable=false
//        replication.factor = 3
//        min.insync.replicas = 2
//        replication.factor > min.insync.replicas
//        enable.auto.commit=false
//        消息处理完成之后再提交位移
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost" + ":" + 9092);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "test");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);
        props.put(ProducerConfig.ACKS_CONFIG,"all");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,1);
        props.put(ProducerConfig.RETRIES_CONFIG,Integer.MAX_VALUE);


        producer = new KafkaProducer(props);
        this.topic = topic;
    }

    public static void main(String[] args) throws InterruptedException {

        Producer producer = new Producer("test");
        new Thread(producer).start();
        producer.join();


    }

    @Override
    public void run() {
        int messageNo = 1;
        while (true && messageNo <= Integer.MAX_VALUE) {
            try {
                producer.send(new ProducerRecord(topic, messageNo ,
                        "23 23"), (metadata, exception) -> {

                    if (exception == null) {

                        log.info("{}",metadata);
                    } else {
                        log.error("{}", ExceptionUtils.getStackTrace(exception));

                    }

                });


                messageNo += 1;

                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
