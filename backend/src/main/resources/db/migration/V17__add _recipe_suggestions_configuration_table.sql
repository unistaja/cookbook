CREATE TABLE RecipeSuggestionsConfiguration (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userId BIGINT UNSIGNED NOT NULL,
  name varchar(40) NOT NULL,
  config JSON NOT NULL,
  PRIMARY KEY(id),
  INDEX RecipeSuggestionsConfiguration_User_FKI(userId),
  FOREIGN KEY RecipeSuggestionsConfiguration_User_FK (userId) REFERENCES User(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_estonian_ci;