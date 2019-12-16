HOST_IP=127.0.0.1

#启动第一个slave
/usr/local/sbin/mesos-slave --master=zk://${HOST_IP}:2181/mesos \
--log_dir=/tmp/mesos/slave/log --work_dir=/tmp/mesos/slave/work  \
--containerizers=docker,mesos  --no-hostname_lookup --ip=${HOST_IP}  \
#--isolation='cgroups/cpu' \
--resources='ports:[1-32000];'