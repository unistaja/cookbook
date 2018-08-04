package ee.cookbook.model;

import javax.persistence.*;

@Entity @IdClass(RatingId.class)
public class Rating {
  @Id
  public Long userId;
  @Id
  public Long recipeId;

  public int rating;
}
