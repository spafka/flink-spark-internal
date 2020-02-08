package io.github.spafka.mysql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import static io.github.spafka.Utils.getStreamEnv;

@Slf4j
public class Replace {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env=getStreamEnv();



        DataStreamSource<String> source = env.addSource(new SourceFunction<String>() {

            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                while (isRunning) {
                    // TimeUnit.MICROSECONDS.sleep(100);
                    sourceContext.collect(String.valueOf(RandomUtils.nextInt(1, 10000000)));
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        }).setParallelism(1);


        source.addSink(new MysqlSigstonRichSinkFunction()).setParallelism(3);


        env.execute("mysql ");

    }
}
