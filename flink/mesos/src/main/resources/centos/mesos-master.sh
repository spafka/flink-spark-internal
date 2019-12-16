HOST_IP=192.168.187.3

/usr/sbin/mesos-master --ip=${HOST_IP} \
 --log_dir=/tmp/master/log --work_dir=/tmp/mesos/master/work \
 --ZK=zk://${HOST_IP}:2181/mesos --quorum=1