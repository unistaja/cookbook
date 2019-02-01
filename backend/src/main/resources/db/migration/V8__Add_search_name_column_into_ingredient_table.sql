ALTER TABLE IngredientLine DROP FOREIGN KEY IngredientLine_ibfk_2;
ALTER TABLE IngredientLine DROP FOREIGN KEY IngredientLine_ibfk_4;
ALTER TABLE AlternateIngredientLine DROP FOREIGN KEY AlternateIngredientLine_ibfk_2;
ALTER TABLE AlternateIngredientLine DROP FOREIGN KEY AlternateIngredientLine_ibfk_4;
ALTER TABLE Ingredient ADD COLUMN searchName VARCHAR(100);
ALTER TABLE Ingredient CHANGE COLUMN name displayName VARCHAR(100);




