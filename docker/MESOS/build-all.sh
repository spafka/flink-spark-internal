#!/usr/bin/env bash

cd java8
docker build -t spafka/java8:v1 ./

cd ../mesos
docker build -t  spafka/mesos:v1 ./

cd ../mesos-master/
docker build -t  spafka/mesos-master:v1 ./

cd ../mesos-slave/
docker build -t  spafka/mesos-slave:v1 ./

cd ../mesos-spark/
docker build -t  spafka/mesos-spark:v1 ./