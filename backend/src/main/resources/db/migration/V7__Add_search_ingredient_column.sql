ALTER TABLE IngredientLine ADD COLUMN searchIngredient VARCHAR(100);
ALTER TABLE IngredientLine ADD INDEX IngredientLine_SearchIngredient_FKI(searchIngredient);
ALTER TABLE IngredientLine ADD FOREIGN KEY IngredientLine_SearchIngredient_FK (searchIngredient) REFERENCES Ingredient(name);

ALTER TABLE AlternateIngredientLine ADD COLUMN searchIngredient VARCHAR(100);
ALTER TABLE AlternateIngredientLine ADD INDEX AlternateIngredientLine_SearchIngredient_FKI(searchIngredient);
ALTER TABLE AlternateIngredientLine ADD FOREIGN KEY AlternateIngredientLine_SearchIngredient_FK (searchIngredient) REFERENCES Ingredient(name);