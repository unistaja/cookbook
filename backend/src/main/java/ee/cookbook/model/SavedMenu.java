package ee.cookbook.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

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
