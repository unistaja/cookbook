package ee.cookbook.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IngredientListName {

  @Id
  public String name;

  public IngredientListName(){}

  public IngredientListName(String name) {
    this.name = name;
  }
}
