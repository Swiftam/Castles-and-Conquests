# Initial schema

# --- !Ups
CREATE TABLE Land (
  id varchar(255) NOT NULL,
  description varchar(255) DEFAULT NULL,
  image varchar(255) DEFAULT NULL,
  income bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  price bigint(20) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Land VALUES
('house','Houses make better homes than huts.','/public/images/land/house.png',30,3,'House',3000),
('hunt','Huts make good homes, if you\'re swamp-dwelling monsters.','/public/images/land/hut.png',15,1,'Hut',1500),
('swamp','It doesn\'t get much better than this.','/public/images/land/swamp.png',10,1,'Swamp',1000),
('tent','Why live in a house when you can live in a tent?','/public/images/land/tent.png',45,5,'Tent',4500);


# --- !Downs
DROP TABLE Land;