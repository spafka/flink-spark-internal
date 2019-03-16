FROM spafka/hadoop:2.7.7

MAINTAINER spafka <ruoyu-chen@foxmail.com>

USER root

#设置环境变量
ENV SPARK_HOME=/root/spark
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin:.
ENV SPARK_BIN_NAME=spark-2.4.0-bin-without-hadoop-scala-2.12

#安装spark 2.1.0
RUN wget https://mirror.bit.edu.cn/apache/spark/spark-2.4.0/${SPARK_BIN_NAME}.tgz && \
    tar -xzvf ${SPARK_BIN_NAME}.tgz -C /root/ && \
    mv /root/$SPARK_BIN_NAME $SPARK_HOME && \
    rm -rf $SPARK_BIN_NAME.tgz && \
    rm -rf $SPARK_HOME/bin/*.cmd && \
    rm -rf $SPARK_HOME/sbin/*.cmd && \
    rm -rf $SPARK_HOME/ec2 &&

#拷贝Spark配置文件
COPY config/spark/* $SPARK_HOME/conf/

CMD [ "sh", "-c", "service sshd start; bash"]