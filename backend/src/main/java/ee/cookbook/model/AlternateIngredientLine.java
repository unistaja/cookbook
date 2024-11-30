package ee.cookbook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public class AlternateIngredientLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  public String ingredient;

  public String searchIngredient;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "unit")
  public IngredientUnit unit;

  @Pattern(regexp = "(\\d+[.,]*\\d*-*\\d*[.,]*\\d*)|(^$)", message = "Invalid amount format (${validatedValue})")
  public String amount;
}
