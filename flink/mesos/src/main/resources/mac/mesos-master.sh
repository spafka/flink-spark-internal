HOST_IP=127.0.0.1

/usr/local/sbin/mesos-master --ip=${HOST_IP} \
 --log_dir=/tmp/master/log --work_dir=/tmp/mesos/master/work \
 --ZK=zk://${HOST_IP}:2181/mesos --quorum=1