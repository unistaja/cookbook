package ee.cookbook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ee.cookbook.model.RecipeSummary;

@Service
public class SavedMenuService {
  @Autowired
  private JdbcTemplate template;

  public List<RecipeSummary> getMenuRecipes(Long userId) {
    RowMapper<RecipeSummary> mapper = (resultSet, index) -> {
        RecipeSummary result = new RecipeSummary();
        result.name = resultSet.getString("name");
        result.recipeId = resultSet.getLong("recipeId");
        result.pictureName = resultSet.getString("pictureName");
        return result;
    };
    return template.query(
        "SELECT r.id as recipeId, r.name, r.pictureName FROM SavedMenu s JOIN Recipe r ON s.recipeId = r.id WHERE s.userId = ?",
        mapper,
        userId
    );
  }
  
}
