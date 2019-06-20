package com.github.spafka.mysql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

@Slf4j
public class Replace {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env;
        if (args.length > 0 && args[0].equals("dev")) {
            Configuration configuration = new Configuration();
            configuration.setInteger(RestOptions.PORT, 8081);
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }


        DataStreamSource<String> source = env.addSource(new SourceFunction<String>() {

            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                while (isRunning) {
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
