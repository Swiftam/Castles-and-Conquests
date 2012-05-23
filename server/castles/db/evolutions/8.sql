# Remove user lands table

# --- !Ups
DROP TABLE UserLand;


# --- !Downs
CREATE TABLE UserLand (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  quantity int(11) NOT NULL,
  land_id varchar(255) DEFAULT NULL,
  user_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY FKF3F478764CB8DAFE (land_id),
  KEY FKF3F4787647140EFE (user_id),
  CONSTRAINT FKF3F4787647140EFE FOREIGN KEY (user_id) REFERENCES User (id),
  CONSTRAINT FKF3F478764CB8DAFE FOREIGN KEY (land_id) REFERENCES Land (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;