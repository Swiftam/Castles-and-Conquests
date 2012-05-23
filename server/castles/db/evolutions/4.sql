# Initial schema

# --- !Ups
CREATE TABLE Unit (
  id varchar(255) NOT NULL,
  defense int(11) DEFAULT NULL,
  image varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  offense int(11) DEFAULT NULL,
  price int(11) DEFAULT NULL,
  upkeep int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO Unit VALUES
('archer',1,'/public/images/units/archer.png','Archer',1,250,1),
('swordsman',0,'/public/images/units/swordsman.png','Swordsman',1,100,1);


# --- !Downs
DROP TABLE Unit;