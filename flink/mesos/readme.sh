#curl -L https://github.com/docker/machine/releases/download/v0.16.2/docker-machine-`uname -s`-`uname -m` >/usr/local/bin/docker-machine &&  chmod +x /usr/local/bin/docker-machine

HOST_IP=127.0.0.1



docker run -itd -p 2181:2181 --name=zookeeper zookeeper:3.4

sleep 10


/usr/local/sbin/mesos-master --ip=${HOST_IP} \
 --log_dir=/tmp/master/log --work_dir=/tmp/mesos/master/work \
 --ZK=zk://${HOST_IP}:2181/mesos --quorum=1

sleep 10


#/usr/local/Cellar/mesos/1.4.1/sbin/mesos-slave --help  查看帮助
#启动第一个slave
/usr/local/sbin/mesos-slave --master=${HOST_IP}:5050 \
--log_dir=/tmp/mesos/slave/log --work_dir=/tmp/mesos/slave/work  \
--containerizers=docker,mesos  --no-hostname_lookup --ip=${HOST_IP} --resources='ports:[1-32000];'

#
#启动第二个slave
#sudo /usr/local/Cellar/mesos/1.4.1/sbin/mesos-slave --master=${HOST_IP}:5050 \
#--log_dir=/Users/lifei/dockerproject/mesos/slave/log2 --work_dir=/Users/lifei/dockerproject/mesos/slave/work2  \
#--containerizers=docker,mesos  --no-hostname_lookup --ip=${HOST_IP} --resources='ports:[1-32000];'


sudo ./bin/start --http_port 8088 --master ${HOST_IP}:5050 --zk zk://${HOST_IP}:2181/marathon -h ${HOST_IP}
