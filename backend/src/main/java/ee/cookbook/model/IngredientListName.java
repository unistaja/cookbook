package ee.cookbook.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IngredientListName {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  public String name;

  public IngredientListName(){}

  public IngredientListName(String name) {
    this.name = name;
  }
}
