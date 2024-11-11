package ee.cookbook.dao;

import ee.cookbook.model.RecipeViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeViewHistoryRepository extends JpaRepository<RecipeViewHistory, Integer> {
    List<RecipeViewHistory> findAllByUserIdOrderByViewTimeAsc(Long userId);
}
