package ee.cookbook.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity @IdClass(SavedMenu.class)
public class SavedMenu implements Serializable {

  public SavedMenu() {}
  public SavedMenu(Long userId, Long recipeId) {
    this.userId = userId;
    this.recipeId = recipeId;
  }
  @Id
  public Long userId;
  @Id
  public Long recipeId;
  
}
