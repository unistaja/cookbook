package ee.cookbook.model;

import javax.persistence.*;

@Entity
public class Rating {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JoinColumn(name = "id")
  public Long id;

  @JoinColumn(name = "userId")
  public Long userId;

  @JoinColumn(name="recipeId")
  public Long recipeId;


  @JoinColumn(name = "rating")
  public int rating;
}
