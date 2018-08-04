package ee.cookbook.dao;

import ee.cookbook.model.PreparedHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PreparedHistoryRepository extends JpaRepository<PreparedHistory, Date> {
  PreparedHistory findTopByRecipeIdAndUserIdOrderByPreparedTimeDesc(Long recipeId, Long userId);
  List<PreparedHistory> findAllByRecipeIdAndUserId(Long recipeId, Long userId);
  List<PreparedHistory> findAllByRecipeId(Long recipeId);
  PreparedHistory findById(Long id);
  void deleteById(Long id);
}
