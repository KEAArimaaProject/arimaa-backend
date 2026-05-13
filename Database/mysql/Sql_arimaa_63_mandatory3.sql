ALTER TABLE `arimaadockermysqldb`.`Matches` 
ADD COLUMN `gold_rating` INT NULL DEFAULT NULL AFTER `player_id_gold`,
ADD COLUMN `silver_rating` INT NULL DEFAULT NULL AFTER `gold_rating`;