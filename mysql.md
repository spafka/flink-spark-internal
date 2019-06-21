CREATE TABLE `t_gb32960SingleMotor` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(17) COLLATE utf8_bin NOT NULL,
  `unixtimestamp` int(32) DEFAULT NULL,
  `motorNumber` int(10) unsigned DEFAULT NULL,
  `motorId` int(11) DEFAULT NULL,
  `motorStatus` int(11) DEFAULT NULL,
  `motorControlTemp` int(11) DEFAULT NULL,
  `engineSpeed` int(11) DEFAULT NULL,
  `engineTorque` float DEFAULT NULL,
  `motorTemp` int(11) DEFAULT NULL,
  `controlInV` float DEFAULT NULL,
  `controlDcI` float DEFAULT NULL,
  `datetime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uid_UNIQUE` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=4201350 DEFAULT CHARSET=utf8 COLLATE=utf8_bin


==>  Preparing: insert into t_gb32960SingleMotor ( uid, unixtimestamp, motorNumber, motorId, motorStatus, motorControlTemp, engineSpeed, engineTorque, motorTemp, controlInV, controlDcI, datetime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE uid= ?, unixtimestamp = ?, motorNumber = ?, motorId= ?, motorStatus= ?, motorControlTemp= ?, engineSpeed= ?, engineTorque= ?, motorTemp= ?, controlInV= ?, controlDcI= ?, datetime= ? 
==> Parameters: LK5A1C1K2GA000194(String), 1560498914(Integer), 1(Integer), 1(Integer), 2(Integer), 46(Integer), -9094(Integer), -501.8(Float), 29(Integer), 215.6(Float), 12.5(Float), 2019-06-15 14:09:19.416(Timestamp), LK5A1C1K2GA000194(String), 1560498914(Integer), 1(Integer), 1(Integer), 2(Integer), 46(Integer), -9094(Integer), -501.8(Float), 29(Integer), 215.6(Float), 12.5(Float), 2019-06-15 14:09:19.416(Timestamp)
<==    Updates: 2


docker run --name mysql -p 3306:3306 -v /usr/local/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:5.6
docker exec -it mysql bash  

mysql -u root -p 
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;  


insert into t_gb32960SingleMotor ( uid, unixtimestamp, motorNumber, motorId, motorStatus, motorControlTemp, engineSpeed, engineTorque, motorTemp, controlInV, controlDcI, datetime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE uid= ?, unixtimestamp = ?, motorNumber = ?, motorId= ?, motorStatus= ?, motorControlTemp= ?, engineSpeed= ?, engineTorque= ?, motorTemp= ?, controlInV= ?, controlDcI= ?, datetime= ? 