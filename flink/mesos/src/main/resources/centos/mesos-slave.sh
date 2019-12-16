HOST_IP=192.168.187.3
# @see https://blog.csdn.net/qq_35440040/article/details/78437935
#启动第一个slave
/usr/sbin/mesos-slave --master=zk://${HOST_IP}:2181/mesos \
--log_dir=/tmp/mesos/slave/log --work_dir=/tmp/mesos/slave/work  \
--containerizers=docker,mesos  --no-hostname_lookup --ip=${HOST_IP}  \
--isolation='cgroups/cpu,cgroups/mem' \
--cgroups_enable_cfs \
--resources='ports:[1-32000];'