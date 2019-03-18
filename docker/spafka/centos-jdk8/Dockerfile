FROM centos:7
MAINTAINER spafka <spafka@apache.com>
USER root
RUN yum update -y && \
    yum install -y libcurl  libcurl-devel apr-devel  cyrus-sasl*  apr-util-devel  zlib* python-devel libevent* && \
    rpm -Uvh http://repos.mesosphere.io/el/7/noarch/RPMS/mesosphere-el-repo-7-1.noarch.rpm && \
    #systemctl stop firewalld && systemctl disable firewalld && \
    yum -y install mesos marathon && yum -y install mesosphere-zookeeper
# 定义默认的MESOS环境变量
ENV MESOS_LOG_DIR /var/log/mesos
ENV MESOS_WORK_DIR /var/work/mesos
# 外部挂载
VOLUME /var/log


CMD ["/bin/bash","/usr/sbin/meses-master"]

#docker build -t spafka/mesos:v1
#docker run -it \
#-e MESOS_HOSTNAME=192.168.6.107 \
#-e MESOS_IP=192.168.6.107 \
#-e MESOS_QUORUM=1 \
#-e MESOS_ZK=zk://192.168.6.107:2181/mesos \
#-v "$(pwd)/log/mesos:/var/log/mesos" \
#-v "$(pwd)/work/mesos:/var/tmp/mesos" \
#--name mesos-master --net host --restart always spafka/mesos:v1