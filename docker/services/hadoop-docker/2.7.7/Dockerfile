FROM spafka/centos7-java8:1.0.0

MAINTAINER twinsen <ruoyu-chen@foxmail.com>

USER root

ENV HADOOP_HOME=/root/hadoop
ENV HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$HADOOP_HOME/lib/native/:/root/protobuf/lib
ENV PATH=$PATH:/root/protobuf/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:.

ENV HADOOP_VERSION=2.7.7

# 1. 安装 OpenSSH, OpenSSL, bzip2-devel
# 2. 同时配置SSH免密钥登陆
RUN yum install -y openssh openssh-server openssh-clients openssl openssl-devel bzip2-devel wget  && \
    yum clean all && \
    ssh-keygen -t rsa -f ~/.ssh/id_rsa -P '' && \
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
    
ADD config/other/ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config && \
    chown root:root /root/.ssh/config

#安装Protocol Buffer, Hadoo$
RUN wget https://http://mirror.bit.edu.cn/apache/hadoop/core/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz && \
	tar -xzvf hadoop-$HADOOP_VERSION.tar.gz -C /root/ && \
    mv /root/hadoop-$HADOOP_VERSION $HADOOP_HOME && \
    rm -rf hadoop-$HADOOP_VERSION.tar.gz && \
    rm -rf $HADOOP_HOME/bin/*.cmd && \
    rm -rf $HADOOP_HOME/sbin/*.cmd && \
    rm -rf $HADOOP_HOME/sbin/*all* && \
    rm -rf $HADOOP_CONF_DIR/*.cmd && \
    rm -rf $HADOOP_CONF_DIR/*.template && \
    rm -rf $HADOOP_CONF_DIR/*.example && \
#    wget https://github.com/ruoyu-chen/hadoop-docker/raw/master/dist/protobuf.tar.gz && \
#	tar -xzvf protobuf.tar.gz -C /root/ && \
#	rm -rf protobuf.tar.gz

#拷贝环境变量配置文件
ADD config/other/bashrc /root/.bashrc

#拷贝Hadoop配置文件
ADD config/hadoop/* $HADOOP_HOME/etc/hadoop/

#建立HDFS目录，日志目录. 格式化NameNode
#RUN mkdir -p /works/hadoop/dfs/name && \ 
#    mkdir -p /works/hadoop/dfs/data && \
#    mkdir -p /works/hadoop/dfs/namesecondary && \
#    mkdir $HADOOP_HOME/logs && \
#    $HADOOP_HOME/bin/hdfs namenode -format

CMD [ "sh", "-c", "service sshd start; bash"]