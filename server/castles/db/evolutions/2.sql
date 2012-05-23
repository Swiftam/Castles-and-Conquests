# Initial schema

# --- !Ups
CREATE TABLE `Level` (
  id int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  xp bigint(20) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Level VALUES
(1,'Newbie',5),
(2,'Student',5),
(3,'Freshman',10),
(4,'Sophomore',25),
(5,'Junior',50),
(6,'Senior',80),
(7,'Graduate',150),
(8,'Apprentice',250),
(9,'Journeyman',500),
(10,'Master',1200);

# --- !Downs
DROP TABLE `Level`;