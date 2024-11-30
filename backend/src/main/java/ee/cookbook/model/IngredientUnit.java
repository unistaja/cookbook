package ee.cookbook.model;

import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class IngredientUnit {
  @Id
  public String name;

  public IngredientUnit() {}

  public IngredientUnit(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
