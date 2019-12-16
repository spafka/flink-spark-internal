
HOST_IP=127.0.0.1



docker run -itd -p 2181:2181 --restart always --name=zookeeper zookeeper:3.4.14