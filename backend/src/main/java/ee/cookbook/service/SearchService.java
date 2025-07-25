package ee.cookbook.service;


import ee.cookbook.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchService {
  @Autowired
  private JdbcTemplate template;

  public SearchResult findRecipes(SearchForm searchParameters, long userId) {
    List<Object> parameters = new ArrayList<>();
    StringBuilder query = new StringBuilder();
    SearchResult result = new SearchResult();
    query.append("SELECT DISTINCT SQL_CALC_FOUND_ROWS Recipe.id, username, Recipe.name, added, pictureName, preparedTime, PreparedHistory.userId, rating, averageRating FROM Recipe JOIN User ON Recipe.userId = User.id LEFT JOIN PreparedHistory ON PreparedHistory.preparedTime = (SELECT MAX(preparedTime) FROM PreparedHistory WHERE recipeId = Recipe.id AND userId = ?) LEFT JOIN Rating ON Recipe.id = Rating.recipeId AND Rating.userId = ? LEFT JOIN average_rating ON Recipe.id = average_rating.recipeId WHERE");
    parameters.add(userId);
    parameters.add(userId);
    if (!StringUtils.isBlank(searchParameters.name)) {
      if (searchParameters.exactName) {
        query.append(" name = ? AND ");
        parameters.add(searchParameters.name);
      } else {
        query.append(" name LIKE ? AND ");
        parameters.add("%" + searchParameters.name + "%");
      }
    }
    if (!StringUtils.isBlank(searchParameters.username)) {
      query.append(" username = ? AND ");
      parameters.add(searchParameters.username);
    }
    if (searchParameters.source != null && !StringUtils.isBlank(searchParameters.source.name)) {
      query.append(" source = ? AND ");
      parameters.add(searchParameters.source.name);
    }
    if (searchParameters.categories.size() > 0) {
      for (String category : searchParameters.categories) {
        query.append(" EXISTS (SELECT * FROM RecipeCategory  WHERE recipeId = Recipe.id AND category = ? ) AND ");
        parameters.add(category);
      }
    }
    if (searchParameters.withIngredients.size() > 0) {
      for (IngredientLine ingredientLine : searchParameters.withIngredients) {
        query.append(" EXISTS (SELECT * FROM IngredientList WHERE recipeId = Recipe.id AND EXISTS (SELECT * FROM IngredientLine WHERE listId = IngredientList. id AND  (searchIngredient = ? OR EXISTS (SELECT * FROM AlternateIngredientLine WHERE lineId = IngredientLine. id AND searchIngredient = ?  ) ");
        parameters.add(ingredientLine.ingredient);
        parameters.add(ingredientLine.ingredient);
        if (ingredientLine.alternateLines.size() > 0) {
          for (AlternateIngredientLine altLine : ingredientLine.alternateLines) {
            query.append("OR searchIngredient = ? OR EXISTS (SELECT * FROM AlternateIngredientLine WHERE lineId = IngredientLine. id AND searchIngredient = ?  ) ");
            parameters.add(altLine.ingredient);
            parameters.add(altLine.ingredient);
          }
        }
        query.append(") ) ) AND ");
      }
    }
    if (searchParameters.withoutIngredients.size() > 0) {
      query.append(" NOT EXISTS (SELECT * FROM IngredientList WHERE recipeId = Recipe.id AND EXISTS (SELECT * FROM IngredientLine WHERE listId = IngredientList. id AND (");
      for (String ingredient : searchParameters.withoutIngredients) {
        query.append("(searchIngredient = ? AND NOT EXISTS (SELECT * FROM AlternateIngredientLine WHERE lineId = IngredientLine. id ");
        parameters.add(ingredient);
        for (String ingr : searchParameters.withoutIngredients) {
          query.append("AND NOT searchIngredient = ? ");
          parameters.add(ingr);
        }
        query.append(") ) OR ");
      }
      query.delete(query.length() - 3, query.length());
      query.append(") ) ) AND ");
    }
    if (searchParameters.hasPicture) {
      query.append(" NOT pictureName = '' AND ");
    }
    if (searchParameters.hasPrepared != null) {
      if (searchParameters.hasPrepared) {
        query.append(" PreparedHistory.userId = ? AND ");
        parameters.add(userId);
      } else {
        query.append(" PreparedHistory.userId IS NULL AND ");
      }
    }
    query.delete(query.length() - 5, query.length());
    if (searchParameters.sortOrder == 0) {
      query.append(" ORDER BY name");
    } else if (searchParameters.sortOrder == 1) {
      query.append(" ORDER BY username");
    } else if (searchParameters.sortOrder == 2) {
      query.append(" ORDER BY added");
    } else if (searchParameters.sortOrder == 3) {
      query.append(" ORDER BY preparedTime");
    } else if (searchParameters.sortOrder == 4) {
      query.append(" ORDER BY RAND()");
    }

    if (searchParameters.descending && searchParameters.sortOrder != 4) {
      query.append(" DESC");
    }
    // ensure recipes with equal sort values are sorted in logical order
    query.append(", id");
    if (searchParameters.descending) {
      query.append(" DESC");
    }

    if (searchParameters.resultsPerPage > 0) {
      query.append(" LIMIT ");
      query.append(searchParameters.resultPage * searchParameters.resultsPerPage);
      query.append(", ");
      query.append(searchParameters.resultsPerPage);
    }
    String sql = query.toString();
    result.recipes = template.query(sql, new RowMapper<Recipe>() {
      @Override
      public Recipe mapRow(ResultSet resultSet, int i) throws SQLException {
        Recipe result = new Recipe();
        PreparedHistory preparedHistory = new PreparedHistory();
        Rating rating = new Rating();
        result.id = resultSet.getLong("id");
        result.name = resultSet.getString("name");
        result.user = new User();
        result.user.username = resultSet.getString("username");
        result.added = resultSet.getTimestamp("added");
        result.pictureName = resultSet.getString("pictureName");
        preparedHistory.preparedTime = resultSet.getDate("preparedTime");
        if (preparedHistory.preparedTime != null) {
          result.preparedHistory = Collections.singletonList(preparedHistory);
        }
        rating.rating = resultSet.getInt("rating");
        result.rating = Collections.singletonList(rating);
        result.averageRating = resultSet.getDouble("averageRating");
        return result;
      }
    }, parameters.toArray());
    if (searchParameters.resultsPerPage > 0) {
      result.total = template.queryForObject("SELECT FOUND_ROWS();", Long.class);
    } else {
      result.total = result.recipes.size();
    }
    return result;
  }

  public List<Long> findRecipeIdsByName(String name) {
    return template.queryForList("SELECT id FROM recipe WHERE name = ?;", Long.class, name);
  }
}
