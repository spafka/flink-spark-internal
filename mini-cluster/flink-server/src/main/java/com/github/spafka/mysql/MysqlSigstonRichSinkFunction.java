package com.github.spafka.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlSigstonRichSinkFunction extends RichSinkFunction<String> {

   transient Connection connection;

   transient DruidDataSource dataSource;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/gb32960data?useSSL=false");
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
        java.lang.String sql = "insert into t_gb32960SingleMotor ( uid, unixtimestamp, motorNumber, motorId, motorStatus, motorControlTemp, engineSpeed, engineTorque, motorTemp, controlInV, controlDcI, datetime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE unixtimestamp = ?, motorNumber = ?, motorId= ?, motorStatus= ?, motorControlTemp= ?, engineSpeed= ?, engineTorque= ?, motorTemp= ?, controlInV= ?, controlDcI= ?, datetime= ? ";

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
            stmt.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));
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
            stmt.setTimestamp(23, new java.sql.Timestamp(System.currentTimeMillis()));


            stmt.execute();
        } catch (SQLException e) {

        }

    }
}
