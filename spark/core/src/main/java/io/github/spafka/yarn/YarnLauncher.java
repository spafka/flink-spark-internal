package io.github.spafka.yarn;


import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.util.HashMap;

/**
 *
 * idea 里提交 spark cluster任務
 * 环境变量 -D SPARK_HOME=E:\spark -D HADOOP_USER_NAME=root
 */
public class YarnLauncher {


    public static void main(String[] args) throws Exception {

        System.setProperty("SPARK_HOME","E:\\spark-2.4.3-bin-hadoop2.7");


        HashMap<String,String> map = new HashMap<String, String>();

        // 设置 processbuild 子程序的环境变量
        map.put("HADOOP_CONF_DIR","D:\\OpenAi\\Apache\\flink-spark-internal\\spark\\core\\src\\main\\resources");


        SparkAppHandle handler =     new SparkLauncher(map)
                .setAppResource("D:\\OpenAi\\Apache\\flink-spark-internal\\spark\\core\\target\\core-1.0.jar") // 必须使用绝对地址,这是 process的环境参数
                .setMainClass("org.apache.spark.examples.SparkPi")
                .setMaster("yarn-cluster")
                .setConf(SparkLauncher.DRIVER_MEMORY, "2g")
                .setConf("spark.yarn.jars","hdfs:///usr/root/spark/2.4.3/*.jar") // spark归档文件
                .setVerbose(true).startApplication((new SparkAppHandle.Listener(){
            @Override
            public void stateChanged(SparkAppHandle handle) {
                System.out.println("**********  state  changed  **********");
            }

            @Override
            public void infoChanged(SparkAppHandle handle) {
                System.out.println("**********  info  changed  **********");
            }
        }));


        while(!"FINISHED".equalsIgnoreCase(handler.getState().toString()) && !"FAILED".equalsIgnoreCase(handler.getState().toString())){
            System.out.println("id    "+handler.getAppId());
            System.out.println("state "+handler.getState());

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

