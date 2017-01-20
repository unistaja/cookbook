package ee.cookbook.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IngredientUnit {
  @Id
  public String name;

  public IngredientUnit() {}

  public IngredientUnit(String name) {
    this.name = name;
  }
}
