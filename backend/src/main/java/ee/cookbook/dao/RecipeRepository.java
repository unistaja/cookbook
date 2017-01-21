package ee.cookbook.dao;

import ee.cookbook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
  Long countByIdAndUserId(Long id, Long userId);

}
