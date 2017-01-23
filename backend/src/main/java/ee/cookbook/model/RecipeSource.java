package ee.cookbook.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RecipeSource {
  @Id
  public String name;

  public RecipeSource() {}

  public RecipeSource(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
