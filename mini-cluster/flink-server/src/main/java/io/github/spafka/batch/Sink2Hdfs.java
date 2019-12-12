package io.github.spafka.batch;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.fs.StringWriter;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;

import java.util.Properties;

public class Sink2Hdfs {

    public static void main(String[] args) throws Exception {

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        Properties properties = new Properties();

        DataStreamSource<String> socketStream = env.socketTextStream("localhost", 9999);


        BucketingSink<String> bucketingSink = new BucketingSink<>("/var"); //hdfs上的路径

        bucketingSink.setWriter(new StringWriter<>())
                .setBatchSize(1024 * 1024 )
                .setBatchRolloverInterval(2000);

        socketStream.addSink(bucketingSink);

        env.execute("test");
    }

}
