# User model

# --- !Ups
CREATE TABLE `User` (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  created datetime DEFAULT NULL,
  exp bigint(20) DEFAULT NULL,
  gold bigint(20) DEFAULT NULL,
  health bigint(20) DEFAULT NULL,
  healthMax bigint(20) DEFAULT NULL,
  lastIncome datetime DEFAULT NULL,
  lastUpdate datetime DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  netWorth bigint(20) DEFAULT NULL,
  snid varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY snid (snid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# --- !Downs
DROP TABLE `User`;