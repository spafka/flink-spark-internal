FROM spafka/mesos:v1

#Docker
RUN yum update -y

ENV MESOS_CONTAINERIZERS docker,mesos
ENV MESOS_EXECUTOR_REGISTRATION_TIMEOUT 5mins

CMD ["mesos-slave", "--no-systemd_enable_support"]