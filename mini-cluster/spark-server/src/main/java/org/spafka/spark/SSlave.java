package org.spafka.spark;

import org.apache.spark.deploy.worker.Worker;

public class SSlave {

    public static void main(String[] args) {
        Worker.main(new String[]{"spark://0.0.0.0:7077"});
    }
}
