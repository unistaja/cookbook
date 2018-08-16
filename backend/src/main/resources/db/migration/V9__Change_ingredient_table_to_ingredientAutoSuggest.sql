RENAME TABLE ingredient TO ingredientAutoSuggest;
ALTER TABLE ingredientline DROP FOREIGN KEY ingredientline_ibfk_2;
ALTER TABLE ingredientline DROP FOREIGN KEY ingredientline_ibfk_4;
ALTER TABLE alternateingredientline DROP FOREIGN KEY alternateingredientline_ibfk_2;
ALTER TABLE alternateingredientline DROP FOREIGN KEY alternateingredientline_ibfk_4;

DELIMITER |
CREATE TRIGGER addIngredientAfterUpdate AFTER UPDATE ON ingredientline
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM ingredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE ingredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO ingredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
CREATE TRIGGER addIngredientAfterInsert AFTER INSERT ON ingredientline
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM ingredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE ingredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO ingredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
CREATE TRIGGER addAltIngredientAfterUpdate AFTER UPDATE ON alternateingredientline
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM ingredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE ingredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO ingredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
CREATE TRIGGER addAltIngredientAfterInsert AFTER INSERT ON alternateingredientline
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM ingredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE ingredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO ingredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
DELIMITER ;