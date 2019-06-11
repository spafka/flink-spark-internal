[hostname]
```bash
hostnamectl set-hostname  master
hostnamectl set-hostname  slave01
hostnamectl set-hostname  slave02

echo "192.168.190.3 master" >> /etc/hosts
echo "192.168.190.4 slave01" >> /etc/hosts
echo "192.168.190.5 slave02" >> /etc/hosts

```
[firewalld]
```bash
systemctl stop firewalld
systemctl disable firewalld
```

[docker]
```bash
yum install -y yum-utils device-mapper-persistent-data lvm2
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install docker-ce -y

 systemctl start docker
 systemctl enable docker

docker veriosn 
echo "{ \"registry-mirrors\": [\"http://hub-mirror.c.163.com\"]}" >  /etc/docker/daemon.json


systemctl daemon-reload
systemctl restart docker
```

[zk on master]
```bash
docker run --restart always --privileged=true -d --name zookeeper --publish 2181:2181  -d zookeeper:3.4.14 
```

[mesos && yum ]
```bash
rpm -Uvh http://repos.mesosphere.io/el/7/noarch/RPMS/mesosphere-el-repo-7-1.noarch.rpm
yum -y install mesos
echo "zk://master:2181/mesos" > /etc/mesos/zk
echo "1" > /etc/mesos-master/quorum
echo "192.168.190.3" > /etc/mesos-master/ip
echo "master" > /etc/mesos-master/hostname

echo "docker,mesos" > /etc/mesos-slave/containerizers
echo "5mins" > /etc/mesos-slave/executor_registration_timeout
```

[mesos master]
```bash
systemctl stop mesos-slave.service
systemctl disable mesos-slave.service
systemctl start mesos-master.service
systemctl enable mesos-master.service

```
[mesos slave]
```bash
systemctl start mesos-slave.service
systemctl enable mesos-slave.service
```
