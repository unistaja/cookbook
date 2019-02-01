RENAME TABLE Ingredient TO IngredientAutoSuggest;

DELIMITER |
CREATE TRIGGER addIngredientAfterUpdate AFTER UPDATE ON IngredientLine
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM IngredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE IngredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO IngredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
CREATE TRIGGER addIngredientAfterInsert AFTER INSERT ON IngredientLine
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM IngredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE IngredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO IngredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
CREATE TRIGGER addAltIngredientAfterUpdate AFTER UPDATE ON AlternateIngredientLine
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM IngredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE IngredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO IngredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
CREATE TRIGGER addAltIngredientAfterInsert AFTER INSERT ON AlternateIngredientLine
FOR EACH ROW
  BEGIN
    IF EXISTS (SELECT * FROM IngredientAutoSuggest WHERE displayName = NEW.ingredient) THEN
      UPDATE IngredientAutoSuggest SET searchName = NEW.searchIngredient WHERE displayName = NEW.ingredient;
    ELSE
      INSERT INTO IngredientAutoSuggest VALUES (NEW.ingredient, NEW.searchIngredient);
    END IF;
  END;
|
DELIMITER ;