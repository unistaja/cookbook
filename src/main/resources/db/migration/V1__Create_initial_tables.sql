
CREATE TABLE Ingredient (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE IngredientUnit (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE IngredientListName (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE Category (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE User (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  passwordHash CHAR(60) NOT NULL,
  active BOOL NOT NULL DEFAULT false,
  PRIMARY KEY(id)
);

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
);

CREATE TABLE IngredientList (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nameId BIGINT UNSIGNED NOT NULL,
  recipeId BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(id),
  INDEX IngredientList_IngredientListName_FKI(nameId),
  FOREIGN KEY IngredientList_IngredientListName_FK (nameId) REFERENCES IngredientListName(id),
  INDEX IngredientList_Recipe_FKI(recipeId),
  FOREIGN KEY IngredientList_Recipe_FK (recipeId) REFERENCES Recipe(id)
);

CREATE TABLE IngredientLine (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  listId BIGINT UNSIGNED NOT NULL,
  ingredientId BIGINT UNSIGNED NOT NULL,
  unitId BIGINT UNSIGNED DEFAULT NULL,
  amount FLOAT NULL,
  PRIMARY KEY(id),
  INDEX IngredientLine_IngredientList_FKI(listId),
  FOREIGN KEY IngredientLine_IngredientList_FK (listId) REFERENCES IngredientList(id),
  INDEX IngredientLine_Ingredient_FKI(ingredientId),
  FOREIGN KEY IngredientLine_Ingredient_FK (ingredientId) REFERENCES Ingredient(id),
  INDEX IngredientLine_IngredientUnit_FKI(unitId),
  FOREIGN KEY IngredientLine_IngredientUnit_FK (unitId) REFERENCES IngredientUnit(id)
);

CREATE TABLE RecipeCategory (
  recipeId BIGINT UNSIGNED NOT NULL,
  categoryId BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(recipeId, categoryId),
  INDEX RecipeCategory_Recipe_FKI(recipeId),
  FOREIGN KEY RecipeCategory_Recipe_FK (recipeId) REFERENCES Recipe(id),
  INDEX RecipeCategory_Category_FKI(categoryId),
  FOREIGN KEY RecipeCategory_Category_FK (categoryId) REFERENCES Category(id)
);

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
);


