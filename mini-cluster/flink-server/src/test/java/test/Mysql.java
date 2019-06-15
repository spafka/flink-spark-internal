package test;


import com.alibaba.druid.pool.DruidDataSource;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Mysql {

    Connection connection;

    DruidDataSource dataSource;

    @BeforeSuite
    public void init() throws SQLException {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://192.168.190.3:3306/test?useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        connection = dataSource.getConnection();

    }

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void TestQps() {


        for (int i = 1; i <= 100; i++) {


            String sql="insert into t_gb32960SingleMotor ( uid, unixtimestamp, motorNumber, motorId, motorStatus, motorControlTemp, engineSpeed, engineTorque, motorTemp, controlInV, controlDcI, datetime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE uid= ?, unixtimestamp = ?, motorNumber = ?, motorId= ?, motorStatus= ?, motorControlTemp= ?, engineSpeed= ?, engineTorque= ?, motorTemp= ?, controlInV= ?, controlDcI= ?, datetime= ? ";

            try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, "LK5A1C1K2GA000194");
                stmt.setInt(2,1560498913);
                stmt.setInt(3,1);
                stmt.setInt(4,1);
                stmt.setInt(5,46);
                stmt.setInt(6,-9094);
                stmt.setFloat(7,-501.8f);
                stmt.setInt(8,29);
                stmt.setInt(9,29);
                stmt.setFloat(10,215.6f);
                stmt.setFloat(11,12.5f);
                stmt.setDate(12,new java.sql.Date(System.currentTimeMillis()));
                stmt.setString(13, "LK5A1C1K2GA000194");
                stmt.setInt(14,1560498914);
                stmt.setInt(15,1);
                stmt.setInt(16,1);
                stmt.setInt(17,46);
                stmt.setInt(18,-9094);
                stmt.setFloat(19,-501.8f);
                stmt.setInt(20,29);
                stmt.setInt(21,29);
                stmt.setFloat(22,215.6f);
                stmt.setFloat(23,12.5f);
                stmt.setDate(24,new java.sql.Date(System.currentTimeMillis()));


                int i1 = stmt.executeUpdate();
                System.out.println(i1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " " + new Date());


    }

}
