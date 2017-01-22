CREATE TABLE AlternateIngredientLine (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  lineId BIGINT UNSIGNED NOT NULL,
  ingredient VARCHAR(100) NOT NULL,
  unit VARCHAR(40) DEFAULT NULL,
  amount FLOAT NULL,
  PRIMARY KEY(id),
  INDEX AlternateIngredientLine_IngredientLine_FKI(lineId),
  FOREIGN KEY AlternateIngredientLine_IngredientLine_FK (lineId) REFERENCES IngredientLine(id),
  INDEX AlternateIngredientLine_Ingredient_FKI(ingredient),
  FOREIGN KEY AlternateIngredientLine_Ingredient_FK (ingredient) REFERENCES Ingredient(name),
  INDEX AlternateIngredientLine_IngredientUnit_FKI(unit),
  FOREIGN KEY AlternateIngredientLine_IngredientUnit_FK (unit) REFERENCES IngredientUnit(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
