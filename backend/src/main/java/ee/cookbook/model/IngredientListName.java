package ee.cookbook.model;

import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class IngredientListName {

  @Id
  public String name;

  public IngredientListName(){}

  public IngredientListName(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
