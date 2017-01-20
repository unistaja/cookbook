package ee.cookbook.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Ingredient {

  @Id
  public String name;

  public Ingredient() {}

  public Ingredient(String name) {
    this.name = name;
  }
}
