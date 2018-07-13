ALTER TABLE ingredientline ADD COLUMN searchIngredient VARCHAR(100);
ALTER TABLE ingredientline ADD INDEX IngredientLine_SearchIngredient_FKI(searchIngredient);
ALTER TABLE ingredientline ADD FOREIGN KEY IngredientLine_SearchIngredient_FK (searchIngredient) REFERENCES Ingredient(name);

ALTER TABLE alternateingredientline ADD COLUMN searchIngredient VARCHAR(100);
ALTER TABLE alternateingredientline ADD INDEX AlternateIngredientLine_SearchIngredient_FKI(searchIngredient);
ALTER TABLE alternateingredientline ADD FOREIGN KEY AlternateIngredientLine_SearchIngredient_FK (searchIngredient) REFERENCES Ingredient(name);