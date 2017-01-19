package ee.cookbook.model;

import javax.persistence.*;

@Entity
public class IngredientLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

//  public long listId;

  @ManyToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "ingredientId")
  public Ingredient ingredient;

  @ManyToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "unitId")
  public IngredientUnit unit;

  public Double amount;
}
