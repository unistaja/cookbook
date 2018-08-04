package ee.cookbook.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PreparedHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public Long userId;

  public Long recipeId;

  public Date preparedTime;
}
