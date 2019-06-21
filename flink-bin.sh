#!/usr/bin/env bash


export FLINK_VERSION=1.8.0
export SCALA_VERSION=2.12
export FLINK_BIN_TAR=flink-$FLINK_VERSION-bin-scala_$SCALA_VERSION.tgz

curl -L -O http://mirror.bit.edu.cn/apache/flink/flink-$FLINK_VERSION/$FLINK_BIN_TAR

tar -zxvf $FLINK_BIN_TAR


export FLINK_HOME=$PWD/flink_$FLINK_VERSION


 echo export FLINK_CONF_DIR=$FLINK_HOME/conf >> ~/.bash_profile
 echo export FLINK_LIB_DIR=$FLINK_HOME/lib >> ~/.bash_profile
 echo export FLINK_BIN_DIR=$FLINK_HOME/bin >> ~/.bash_profile
 echo export FLINK_HOME=$FLINK_HOME >> ~/.bash_profile
 source ~/.bash_profile


rm -rf $FLINK_BIN_TAR