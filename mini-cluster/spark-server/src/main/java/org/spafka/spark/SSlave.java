package org.spafka.spark;

import org.apache.spark.deploy.worker.Worker;

public class SSlave {

    public static void main(String[] args) {

        System.setProperty("SPARK_HOME","/tmp");
        Worker.main(new String[]{"spark://0.0.0.0:7077"});
    }
}
