package ee.cookbook.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
public class IngredientLine {
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

  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinColumn(name = "lineId", nullable = false)
  public List<AlternateIngredientLine> alternateLines;

}
