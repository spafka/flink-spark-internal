package org.spafka.spark;

import org.apache.spark.deploy.master.Master;

public class SMaster {

    public static void main(String[] args) {

        System.setProperty("SPARK_MASTER_IP", "localhost");
        Master.main(args);
    }
}
