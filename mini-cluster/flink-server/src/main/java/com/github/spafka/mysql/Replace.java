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
        if (args.length>0 && args[0].equals("dev")){
            Configuration configuration = new Configuration();
            configuration.setInteger(RestOptions.PORT,8081);
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        }else {
            env=StreamExecutionEnvironment.getExecutionEnvironment();
        }




        DataStreamSource<String> source = env.addSource(new SourceFunction<String>() {

            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                while (isRunning) {

                    TimeUnit.MILLISECONDS.sleep(100);
                    sourceContext.collect(String.valueOf(RandomUtils.nextInt(1,100)));
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        }).setParallelism(1);


        source.addSink(new RichSinkFunction<String>() {


            Connection connection;

            DruidDataSource dataSource;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);

                dataSource = new DruidDataSource();
                dataSource.setUrl("jdbc:mysql://192.168.190.3:3306/test?useSSL=false");
                dataSource.setUsername("root");
                dataSource.setPassword("root");

                connection = dataSource.getConnection();
            }

            @Override
            public void close() throws Exception {
                super.close();
            }

            @Override
            public void invoke(String value) throws Exception {
                String sql = "insert into t_gb32960SingleMotor ( uid, unixtimestamp, motorNumber, motorId, motorStatus, motorControlTemp, engineSpeed, engineTorque, motorTemp, controlInV, controlDcI, datetime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE unixtimestamp = ?, motorNumber = ?, motorId= ?, motorStatus= ?, motorControlTemp= ?, engineSpeed= ?, engineTorque= ?, motorTemp= ?, controlInV= ?, controlDcI= ?, datetime= ? ";

                try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, value);
                    stmt.setInt(2, 1560498913);
                    stmt.setInt(3, 1);
                    stmt.setInt(4, 1);
                    stmt.setInt(5, 46);
                    stmt.setInt(6, -9094);
                    stmt.setFloat(7, -501.8f);
                    stmt.setInt(8, 29);
                    stmt.setInt(9, 29);
                    stmt.setFloat(10, 215.6f);
                    stmt.setFloat(11, 12.5f);
                    stmt.setDate(12, new java.sql.Date(System.currentTimeMillis()));
                    stmt.setInt(13, (int) System.currentTimeMillis());
                    stmt.setInt(14, 1);
                    stmt.setInt(15, 1);
                    stmt.setInt(16, 46);
                    stmt.setInt(17, -9094);
                    stmt.setFloat(18, -501.8f);
                    stmt.setInt(19, 29);
                    stmt.setInt(20, 29);
                    stmt.setFloat(21, 215.6f);
                    stmt.setFloat(22, 12.5f);
                    stmt.setDate(23, new java.sql.Date(System.currentTimeMillis()));


                    int i1 = stmt.executeUpdate();
                    System.out.println(i1);

                } catch (SQLException e) {

                }

            }


        });


        env.execute("mysql ");

    }
}
