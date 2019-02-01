CREATE TABLE Rating (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userId BIGINT UNSIGNED NOT NULL,
  recipeId BIGINT UNSIGNED NOT NULL,
  rating TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX Rating_Recipe_FKI(recipeId),
  FOREIGN KEY Rating_Recipe_FK (recipeId) REFERENCES Recipe(id),
  INDEX Rating_User_FKI(userId),
  FOREIGN KEY Rating_User_FK (userId) REFERENCES User(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_estonian_ci;

CREATE VIEW average_rating AS
  SELECT AVG(rating) AS averageRating, recipeId
  FROM Rating
  GROUP BY recipeId;