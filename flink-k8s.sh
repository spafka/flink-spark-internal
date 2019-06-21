#!/usr/bin/env bash


rm -rf k8s

mkdir k8s

cd k8s

svn checkout https://github.com/apache/flink/trunk/flink-container/docker

cd docker
# 注释掉 FLINK_JOB_ARTIFACTS_DIR
sh build.sh --from-release --flink-version 1.8.0 --hadoop-version 2.4 --scala-version 2.12 --image-name flink

svn checkout https://github.com/apache/flink/trunk/flink-container/kubernetes