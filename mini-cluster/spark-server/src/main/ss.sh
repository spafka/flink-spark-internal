#!/usr/bin/env bash

bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn \
    --deploy-mode cluster \
    examples/jars/spark-examples_2.12-2.4.3.jar


bin/spark-submit --class org.apache.spark.examples.streaming.StatefulNetworkWordCount \
    --master yarn \
    --deploy-mode cluster \
    examples/jars/spark-examples_2.12-2.4.3.jar \
    hadoop10 9999