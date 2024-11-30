package ee.cookbook.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredientautosuggest")
public class Ingredient {

  @Id
  public String displayName;

  public String searchName;

  public Ingredient() {}

  public Ingredient(String name) {
    this.displayName = name;
  }

}
