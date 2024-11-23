CREATE TABLE SavedMenu (
  userId BIGINT UNSIGNED NOT NULL,
  recipeId BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(userId, recipeId),
  INDEX SavedMenu_User_FKI(userId),
  FOREIGN KEY SavedMenu_User_FK (userId) REFERENCES User(id),
  INDEX SavedMenu_Recipe_FKI(recipeId),
  FOREIGN KEY SavedMenu_Recipe_FK (recipeId) REFERENCES Recipe(id)
) ENGINE=InnoDB;