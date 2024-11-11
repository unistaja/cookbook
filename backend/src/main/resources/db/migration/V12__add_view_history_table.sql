CREATE TABLE RecipeViewHistory (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userId BIGINT UNSIGNED NOT NULL,
  recipeId BIGINT UNSIGNED NOT NULL,
  viewTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX RecipeViewHistory_Recipe_FKI(recipeId),
  FOREIGN KEY RecipeViewHistory_Recipe_FK (recipeId) REFERENCES Recipe(id),
  INDEX RecipeViewHistory_User_FKI(userId),
  FOREIGN KEY RecipeViewHistory_User_FK (userId) REFERENCES User(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_estonian_ci;