package ee.cookbook.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@SecondaryTable(name = "average_rating", pkJoinColumns = @PrimaryKeyJoinColumn(name = "recipeId"))
public class Recipe implements Persistable<Long>{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  public String pictureName;

  @ManyToOne
  @JoinColumn(name = "userId")
  public User user;

  @NotNull
  public String instructions;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "source")
  public RecipeSource source;

  @Column(table = "average_rating", updatable = false)
  public Double averageRating;

  @NotNull
  public String name;

  @Column(insertable = false)
  public Date added;

  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinColumn(name = "recipeId", nullable = false, updatable = false, insertable = false)
  public List<PreparedHistory> preparedHistory;

  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinColumn(name = "recipeId", nullable = false, updatable = false, insertable = false)
  public List<Rating> rating;

  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinColumn(name = "recipeId", nullable = false)
  public List<IngredientList> ingredientLists;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinTable(name = "RecipeCategory",
      joinColumns = @JoinColumn(name = "recipeId", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "category", referencedColumnName = "name")
  )
  public List<Category> categories;


  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    //this is needed for hibernate to call merge instead of persist
    return false;
  }
}
