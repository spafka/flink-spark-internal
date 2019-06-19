package com.github.spafka.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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

                    TimeUnit.MILLISECONDS.sleep(1000);
                    sourceContext.collect(String.valueOf(RandomUtils.nextInt(1, 10)));
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
