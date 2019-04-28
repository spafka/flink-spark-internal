FROM spafka/mesos:v1

ARG SPARK_BINARY=spark-2.4.0-bin-without-hadoop-scala-2.12.tgz

RUN yum install wget -y && \
    mkdir -p /spark && \
    cd /spark/ && \
    wget https://mirrors.tuna.tsinghua.edu.cn/apache/spark/spark-2.4.0/$SPARK_BINARY && \
    tar -xzvf /spark/$SPARK_BINARY -C /spark --strip-components 1

ENV SPARK_HOME /spark
ENV PATH=$PATH:$SPARK_HOME/bin

COPY spark-defaults.conf $SPARK_HOME/conf
COPY bootstrap.sh $SPARK_HOME
COPY entrypoint.sh /

EXPOSE 4040

ENTRYPOINT ["/entrypoint.sh"]