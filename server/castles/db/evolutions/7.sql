# Fixed lands per user

# --- !Ups
ALTER TABLE `User` ADD lands varchar(2000);
ALTER TABLE Land ADD parent varchar(255);



# --- !Downs
ALTER TABLE `User` DROP lands varchar(2000);
ALTER TABLE Land DROP parent;