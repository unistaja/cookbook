package ee.cookbook.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
public class AlternateIngredientLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "ingredient")
  public Ingredient ingredient;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "searchIngredient")
  public Ingredient searchIngredient;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "unit")
  public IngredientUnit unit;

  @Pattern(regexp = "(\\d+[.,]*\\d*-*\\d*[.,]*\\d*)|(^$)", message = "Invalid amount format")
  public String amount;
}
