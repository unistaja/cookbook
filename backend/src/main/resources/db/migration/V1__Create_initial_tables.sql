
CREATE TABLE Ingredient (
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IngredientUnit (
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IngredientListName (
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Category (
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE User (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  passwordHash CHAR(60) NOT NULL,
  active BOOL NOT NULL DEFAULT false,
  PRIMARY KEY(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Recipe (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  pictureName VARCHAR(255) DEFAULT NULL,
  userId BIGINT UNSIGNED NOT NULL,
  instructions TEXT NOT NULL,
  source VARCHAR(255) NULL,
  name VARCHAR(50) NOT NULL,
  added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX Recipe_User_FKI(userId),
  FOREIGN KEY Recipe_User_FK (userId) REFERENCES User(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IngredientList (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name varchar(40) NOT NULL,
  recipeId BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX IngredientList_IngredientListName_FKI(name),
  FOREIGN KEY IngredientList_IngredientListName_FK (name) REFERENCES IngredientListName(name),
  INDEX IngredientList_Recipe_FKI(recipeId),
  FOREIGN KEY IngredientList_Recipe_FK (recipeId) REFERENCES Recipe(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IngredientLine (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  listId BIGINT UNSIGNED NOT NULL,
  ingredient VARCHAR(100) NOT NULL,
  unit VARCHAR(40) DEFAULT NULL,
  amount FLOAT NULL,
  PRIMARY KEY(id),
  INDEX IngredientLine_IngredientList_FKI(listId),
  FOREIGN KEY IngredientLine_IngredientList_FK (listId) REFERENCES IngredientList(id),
  INDEX IngredientLine_Ingredient_FKI(ingredient),
  FOREIGN KEY IngredientLine_Ingredient_FK (ingredient) REFERENCES Ingredient(name),
  INDEX IngredientLine_IngredientUnit_FKI(unit),
  FOREIGN KEY IngredientLine_IngredientUnit_FK (unit) REFERENCES IngredientUnit(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE RecipeCategory (
  recipeId BIGINT UNSIGNED NOT NULL,
  category VARCHAR(40) NOT NULL,
  PRIMARY KEY(recipeId, category),
  INDEX RecipeCategory_Recipe_FKI(recipeId),
  FOREIGN KEY RecipeCategory_Recipe_FK (recipeId) REFERENCES Recipe(id),
  INDEX RecipeCategory_Category_FKI(category),
  FOREIGN KEY RecipeCategory_Category_FK (category) REFERENCES Category(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE PreparedHistory (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  userId BIGINT UNSIGNED NOT NULL,
  recipeId BIGINT UNSIGNED NOT NULL,
  preparedTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  INDEX PreparedHistory_Recipe_FKI(recipeId),
  FOREIGN KEY PreparedHistory_Recipe_FK (recipeId) REFERENCES Recipe(id),
  INDEX PreparedHistory_User_FKI(userId),
  FOREIGN KEY PreparedHistory_User_FK (userId) REFERENCES User(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


