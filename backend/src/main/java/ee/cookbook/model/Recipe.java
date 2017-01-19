package ee.cookbook.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
public class Recipe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  public String pictureName;

  @ManyToOne
  @JoinColumn(name = "userId")
  public User user;

  @NotNull
  public String instructions;

  public String source;

  @NotNull
  public String name;

  public Date added;

  @OneToMany(cascade = {CascadeType.ALL})
  @JoinColumn(name = "recipeId", nullable = false)
  public List<IngredientList> ingredientLists;

  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(name = "RecipeCategory",
      joinColumns = @JoinColumn(name = "recipeId", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "categoryId", referencedColumnName = "id")
  )
  public List<Category> categories;

}
