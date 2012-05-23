# Initial schema

# --- !Ups
CREATE TABLE Quest (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) DEFAULT NULL,
  image varchar(255) DEFAULT NULL,
  maxGold int(11) NOT NULL,
  minGold int(11) NOT NULL,
  name varchar(255) DEFAULT NULL,
  power int(11) NOT NULL,
  xp int(11) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Quest VALUES
(43,'Ogres are terrible things, you should kill them all.','/public/images/quests/ogre.png',10,5,'Fight an Ogre',1,2),
(44,'Help the refugees avoid persecution.','/public/images/quests/hideRefugees.png',15,6,'Hide Refugees',2,3),
(45,'When caught unawares by a giant, you must distract it while your friends escape. RUN!','/public/images/quests/giant.png',12,7,'Escape the Giant',3,4);



# --- !Downs
DROP TABLE Quest;