CREATE TABLE IF NOT EXISTS `users` (
   `id` BIGINT NOT NULL AUTO_INCREMENT,
   `user_name` VARCHAR(255) UNIQUE,
    `full_name` VARCHAR(255),
    `password` VARCHAR(255),
    `account_non_expired` BOOLEAN DEFAULT FALSE,
    `account_non_locked` BOOLEAN DEFAULT FALSE,
    `credentials_non_expired` BOOLEAN DEFAULT FALSE,
    `enabled` BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
