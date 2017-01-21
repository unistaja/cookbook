package ee.cookbook.model;

import com.fasterxml.jackson.annotation.JsonValue;

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

  @JsonValue
  public String getName() {
    return name;
  }
}
