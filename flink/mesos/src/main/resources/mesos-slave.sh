HOST_IP=127.0.0.1

#启动第一个slave
/usr/local/sbin/mesos-slave --master=${HOST_IP}:5050 \
--log_dir=/tmp/mesos/slave/log --work_dir=/tmp/mesos/slave/work  \
--containerizers=docker,mesos  --no-hostname_lookup --ip=${HOST_IP} --resources='ports:[1-32000];'