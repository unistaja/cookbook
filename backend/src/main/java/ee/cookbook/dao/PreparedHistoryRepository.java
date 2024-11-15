package ee.cookbook.dao;

import ee.cookbook.model.PreparedHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface PreparedHistoryRepository extends JpaRepository<PreparedHistory, Date> {
  PreparedHistory findTopByRecipeIdAndUserIdOrderByPreparedTimeDesc(Long recipeId, Long userId);
  List<PreparedHistory> findAllByRecipeIdAndUserIdOrderByPreparedTimeDesc(Long recipeId, Long userId);
  List<PreparedHistory> findAllByRecipeId(Long recipeId);
  PreparedHistory findById(Long id);
  @Transactional
  void deleteById(Long id);
}
