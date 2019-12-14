#export MESOS_NATIVE_JAVA_LIBRARY="/path/to/mesos/lib/libmesos.dylib"

curl -O http://downloads.mesosphere.com/marathon/v1.5.1/marathon-1.5.1.tgz
tar xzf marathon-1.5.1.tgz


./bin/marathon --master zk://localhost:2181/mesos --zk zk://localhost:2181/marathon
