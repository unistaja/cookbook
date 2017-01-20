package ee.cookbook.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Category {

  @Id
  public String name;

  public Category() {}

  public Category(String name) {
    this.name = name;
  }
}
