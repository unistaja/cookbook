package ee.cookbook.model;

import com.fasterxml.jackson.annotation.JsonValue;

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
