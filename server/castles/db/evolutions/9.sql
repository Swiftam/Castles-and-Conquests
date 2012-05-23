# Remove user lands table

# --- !Ups
TRUNCATE TABLE Level;
INSERT INTO Level VALUES
(1,'Newbie',15),
(2,'Student',30),
(3,'Freshman',45),
(4,'Sophomore',60),
(5,'Junior',75),
(6,'Senior',95),
(7,'Graduate',150),
(8,'Apprentice',250),
(9,'Journeyman',500),
(10,'Master',1200);

# --- !Downs
TRUNCATE TABLE Level;
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