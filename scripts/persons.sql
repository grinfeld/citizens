CREATE DATABASE if not exists myimdb;

-- -----------------------------------------------------
-- Table `mydb`.`neighborhoods`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`neighborhoods` (
                                                      `id` INT NOT NULL AUTO_INCREMENT,
                                                      `name` VARCHAR(255) NOT NULL,
                                                      `city` VARCHAR(255) NOT NULL,
                                                      `country` VARCHAR(255) NOT NULL,
                                                      PRIMARY KEY (`id`),
                                                      FULLTEXT INDEX `ft_name` (`name`) VISIBLE,
                                                      FULLTEXT INDEX `ft_coutry` (`country`) VISIBLE,
                                                      FULLTEXT INDEX `ft_city` (`city`) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`cities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`cities` (
                                               `id` INT NOT NULL AUTO_INCREMENT,
                                               `city_name` VARCHAR(255) NOT NULL,
                                               `country` VARCHAR(255) NOT NULL,
                                               PRIMARY KEY (`id`),
                                               FULLTEXT INDEX `ft_city` (`city_name`) VISIBLE,
                                               FULLTEXT INDEX `ft_country` (`country`) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`countries`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`countries` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `name` VARCHAR(255) NOT NULL,
                                                  PRIMARY KEY (`id`),
                                                  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
                                                  FULLTEXT INDEX `ft_country` (`name`) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`addresses` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `country` VARCHAR(255) NOT NULL,
                                                  `city` VARCHAR(255) NOT NULL,
                                                  `street` VARCHAR(255) NOT NULL,
                                                  `building` VARCHAR(5) NULL,
                                                  `apartment` INT(4) NULL,
                                                  `entrance` VARCHAR(4) NULL,
                                                  `neighborhood` VARCHAR(255) NULL,
                                                  PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`infos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`infos` (
                                              `person_id` INT NOT NULL,
                                              `first_name` VARCHAR(45) NULL,
                                              `last_name` VARCHAR(45) NULL,
                                              `middle_name` VARCHAR(45) NULL,
                                              `year` INT(4) NULL,
                                              PRIMARY KEY (`person_id`),
                                              FULLTEXT INDEX `name` (`first_name`, `last_name`, `middle_name`) VISIBLE,
                                              INDEX `year` (`year` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`tags` (
                                             `tag_id` INT NOT NULL AUTO_INCREMENT,
                                             `tag_name` VARCHAR(45) NOT NULL,
                                             `person_id` INT NOT NULL,
                                             PRIMARY KEY (`tag_id`),
                                             FULLTEXT INDEX `tag_name` (`tag_name`) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`emails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`emails` (
                                               `email_id` INT NOT NULL AUTO_INCREMENT,
                                               `person_id` INT NOT NULL,
                                               `email_value` VARCHAR(255) NOT NULL,
                                               PRIMARY KEY (`email_id`),
                                               INDEX `person` () VISIBLE,
                                               FULLTEXT INDEX `email` (`email_value`) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`phone_id`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`phone_id` (
                                                 `phone_id` INT NOT NULL AUTO_INCREMENT,
                                                 `person_id` INT NOT NULL,
                                                 `phone_type` INT(2) NOT NULL,
                                                 `phone_value` VARCHAR(45) NOT NULL,
                                                 PRIMARY KEY (`phone_id`),
                                                 INDEX `person` (`person_id` ASC) VISIBLE,
                                                 INDEX `type` (`phone_type` ASC) VISIBLE,
                                                 FULLTEXT INDEX `value` (`phone_value`) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`persons`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`persons` (
                                                `person_id` INT NOT NULL AUTO_INCREMENT,
                                                `tz` VARCHAR(9) NULL,
                                                `remove` TINYINT NOT NULL DEFAULT 0,
                                                PRIMARY KEY (`person_id`),
                                                FULLTEXT INDEX `tz` (`tz`) VISIBLE)
    ENGINE = InnoDB;