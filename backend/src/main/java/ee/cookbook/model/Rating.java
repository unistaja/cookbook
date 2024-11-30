package ee.cookbook.model;

import jakarta.persistence.*;

@Entity @IdClass(RatingId.class)
public class Rating {
  @Id
  public Long userId;
  @Id
  public Long recipeId;

  public int rating;
}
