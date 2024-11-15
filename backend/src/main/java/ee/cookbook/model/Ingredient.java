package ee.cookbook.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
