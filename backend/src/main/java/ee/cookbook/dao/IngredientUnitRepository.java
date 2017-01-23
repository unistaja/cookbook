package ee.cookbook.dao;

import ee.cookbook.model.IngredientUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientUnitRepository extends JpaRepository<IngredientUnit, String> {
}
