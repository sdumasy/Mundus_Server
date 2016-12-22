-- MySQL Script generated by MySQL Workbench
-- 12/20/16 11:24:17
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE=`TRADITIONAL,ALLOW_INVALID_DATES`;

-- -----------------------------------------------------
-- Table `device`
-- -----------------------------------------------------
CREATE TABLE `device` (
  `device_id` VARCHAR(45) NOT NULL,
  `auth_token` VARCHAR(45) NOT NULL,
  UNIQUE INDEX `token_UNIQUE` (`auth_token` ASC),
  PRIMARY KEY (`device_id`),
  UNIQUE INDEX `user_jd_UNIQUE` (`device_id` ASC));



-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE `role` (
  `role_id` INT(1) NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `id_UNIQUE` (`role_id` ASC));



-- -----------------------------------------------------
-- Table `session_player`
-- -----------------------------------------------------
CREATE TABLE `session_player` (
  `player_id` VARCHAR(45) NOT NULL,
  `device_id` VARCHAR(45) NOT NULL,
  `session_id` VARCHAR(45) NOT NULL,
  `role_id` INT NOT NULL,
  `score` INT(5) NOT NULL,
  PRIMARY KEY (`player_id`),
  UNIQUE INDEX `player_id_UNIQUE` (`player_id` ASC),
  INDEX `fk_session_id_idx` (`session_id` ASC),
  INDEX `fk_session_device_id_idx` (`device_id` ASC),
  INDEX `fk_session_player_role_id_idx` (`role_id` ASC),
  CONSTRAINT `fk_session_id`
  FOREIGN KEY (`session_id`)
  REFERENCES `session` (`session_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_session_device_id`
  FOREIGN KEY (`device_id`)
  REFERENCES `device` (`device_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_session_player_role_id`
  FOREIGN KEY (`role_id`)
  REFERENCES `role` (`role_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE);



-- -----------------------------------------------------
-- Table `session`
-- -----------------------------------------------------
CREATE TABLE `session` (
  `session_id` VARCHAR(45) NOT NULL,
  `player_id` VARCHAR(45) NOT NULL,
  `status` INT(1) NULL,
  `created` DATETIME NULL,
  UNIQUE INDEX `uid_UNIQUE` (`player_id` ASC),
  PRIMARY KEY (`session_id`),
  UNIQUE INDEX `id_UNIQUE` (`session_id` ASC));



-- -----------------------------------------------------
-- Table `session_token`
-- -----------------------------------------------------
CREATE TABLE `session_token` (
  `join_token` VARCHAR(45) NOT NULL,
  `session_id` VARCHAR(45) NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`join_token`),
  UNIQUE INDEX `token_UNIQUE` (`join_token` ASC),
  INDEX `fk_token_session_id_idx` (`session_id` ASC),
  INDEX `fk_token_session_role_id_idx` (`role_id` ASC),
  CONSTRAINT `fk_token_session_id`
  FOREIGN KEY (`session_id`)
  REFERENCES `session` (`session_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_token_session_role_id`
  FOREIGN KEY (`role_id`)
  REFERENCES `role` (`role_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE);


INSERT INTO `role` (`role_id`, `name`) VALUES (0, 'Admin');
INSERT INTO `role` (`role_id`, `name`) VALUES (1, 'Moderator');
INSERT INTO `role` (`role_id`, `name`) VALUES (2, 'User');


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
