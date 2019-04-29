FROM spafka/java8:v1
MAINTAINER spafka <spafka@apache.com>
USER root
RUN yum update -y && \
    yum install -y libcurl  libcurl-devel apr-devel  cyrus-sasl*  apr-util-devel  zlib* python-devel libevent* && \
    rpm -Uvh http://repos.mesosphere.io/el/7/noarch/RPMS/mesosphere-el-repo-7-1.noarch.rpm && \
    #systemctl stop firewalld && systemctl disable firewalld && \
    yum -y install mesos marathon
# 定义默认的MESOS环境变量
ENV MESOS_LOG_DIR /var/log/mesos
ENV MESOS_WORK_DIR /var/work/mesos
ENV ENV MESOS_NATIVE_JAVA_LIBRARY=/usr/lib/libmesos.so
# 外部挂载
VOLUME /var


CMD ["/bin/bash"]
