package ee.cookbook.model;

import javax.persistence.*;

@Entity
public class IngredientLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

//  public long listId;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "ingredient")
  public Ingredient ingredient;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "unit")
  public IngredientUnit unit;

  public Double amount;
}
