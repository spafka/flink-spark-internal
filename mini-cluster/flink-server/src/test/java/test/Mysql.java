package test;


import com.alibaba.druid.pool.DruidDataSource;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    @Test(threadPoolSize = 1, invocationCount = 5)
    public void TestQps() {


        for (int i = 1; i <= 100; i++) {


            String sql="insert into t_gb32960SingleMotor ( uid, unixtimestamp, motorNumber, motorId, motorStatus, motorControlTemp, engineSpeed, engineTorque, motorTemp, controlInV, controlDcI, datetime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE unixtimestamp = ?, motorNumber = ?, motorId= ?, motorStatus= ?, motorControlTemp= ?, engineSpeed= ?, engineTorque= ?, motorTemp= ?, controlInV= ?, controlDcI= ?, datetime= ? ";

            try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, "LK5A1C1K2GA00019");
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
                stmt.setInt(13,(int)System.currentTimeMillis()/1000);
                stmt.setInt(14,1);
                stmt.setInt(15,1);
                stmt.setInt(16,46);
                stmt.setInt(17,-9094);
                stmt.setFloat(18,-501.8f);
                stmt.setInt(19,29);
                stmt.setInt(20,29);
                stmt.setFloat(21,215.6f);
                stmt.setFloat(22,12.5f);
                stmt.setDate(23,new java.sql.Date(System.currentTimeMillis()));


                int i1 = stmt.executeUpdate();
                System.out.println(i1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " " + new Date());


    }

}
