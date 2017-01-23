package ee.cookbook.dao;

import ee.cookbook.model.IngredientListName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientListNameRepository extends JpaRepository<IngredientListName, String> {
}
