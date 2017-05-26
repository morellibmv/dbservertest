CREATE TABLE users (
	`id` INT NOT NULL AUTO_INCREMENT,
	`email` VARCHAR(256) NOT NULL,
	`creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `email` (`email`)
);

CREATE TABLE restaurants (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE INDEX `name` (`name`)
);

CREATE TABLE votes (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user` INT NOT NULL,
  `restaurant` INT NOT NULL,
  `votedate` DATE NOT NULL,
	PRIMARY KEY (`id`),
  CONSTRAINT `FK_VOTES_USERS` FOREIGN KEY(`user`) REFERENCES users(`id`),
  CONSTRAINT `FK_VOTES_RESTAURANTS` FOREIGN KEY(`restaurant`) REFERENCES restaurants(`id`)
);

INSERT INTO `restaurants` (`name`) VALUES ('Sashiburi');

INSERT INTO `restaurants` (`name`) VALUES ('Grelhatus');

INSERT INTO `restaurants` (`name`) VALUES ('Speed');

INSERT INTO `users`(`email`) VALUES ('test@dbserver');

INSERT INTO `votes`( `user`, `restaurant`, `votedate`) values (1,1,DATE_SUB(NOW(), INTERVAL 1 DAY));

commit;