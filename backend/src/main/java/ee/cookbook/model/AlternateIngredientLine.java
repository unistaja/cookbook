package ee.cookbook.model;

import javax.persistence.*;

@Entity
public class AlternateIngredientLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "ingredient")
  public Ingredient ingredient;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "unit")
  public IngredientUnit unit;

  public Double amount;
}
