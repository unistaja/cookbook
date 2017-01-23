package ee.cookbook.dao;

import ee.cookbook.model.RecipeSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeSourceRepository extends JpaRepository<RecipeSource, String> {
}
