package ee.cookbook.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class IngredientList {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "name")
  public IngredientListName name;

  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinColumn(name = "listId", nullable = false)
  public List<IngredientLine> ingredientLines;

//  public long recipeId;

}
