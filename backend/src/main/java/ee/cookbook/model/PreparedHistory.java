package ee.cookbook.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PreparedHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JoinColumn(name = "id")
  public Long id;

  @JoinColumn(name = "userId")
  public Long userId;

  @JoinColumn(name="recipeId")
  public Long recipeId;


  @JoinColumn(name = "preparedTime")
  public Date preparedTime;
}
