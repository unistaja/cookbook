package ee.cookbook.dao;


import ee.cookbook.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
  Rating findByRecipeIdAndUserId(Long recipeId, Long userId);
  List<Rating> findAllByRecipeId(Long recipeId);
}