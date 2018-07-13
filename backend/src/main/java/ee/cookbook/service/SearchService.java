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
import java.util.List;

@Service
public class SearchService {
  @Autowired
  private JdbcTemplate template;

  public SearchResult findRecipes(SearchForm searchParameters) {
    List<String> parameters = new ArrayList<>();
    StringBuilder query = new StringBuilder();
    SearchResult result = new SearchResult();
    query.append("SELECT SQL_CALC_FOUND_ROWS recipe.id, username, recipe.name, added, pictureName, preparedTime, preparedhistory.userid, rating, averageRating FROM recipe JOIN user ON recipe.userId = user.id LEFT JOIN preparedhistory ON preparedhistory.preparedTime = (SELECT MAX(preparedTime) FROM preparedhistory WHERE recipeId = recipe.id AND userId = ?) LEFT JOIN rating ON recipe.id = rating.recipeId AND rating.userId = ? LEFT JOIN average_rating ON recipe.id = average_rating.recipeId WHERE");
    parameters.add(String.valueOf(searchParameters.userId));
    parameters.add(String.valueOf(searchParameters.userId));
    if (!StringUtils.isBlank(searchParameters.name)) {
      query.append(" name = ? AND ");
      parameters.add(searchParameters.name);
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
        query.append(" EXISTS (SELECT * FROM recipecategory WHERE recipeId = recipe.id AND category = ? ) AND ");
        parameters.add(category);
      }
    }
    if (searchParameters.withIngredients.size() > 0) {
      for (IngredientLine ingredientLine : searchParameters.withIngredients) {
        query.append(" EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND  (searchIngredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND searchIngredient = ?  ) ");
        parameters.add(ingredientLine.ingredient.name);
        parameters.add(ingredientLine.ingredient.name);
        if (ingredientLine.alternateLines.size() > 0) {
          for (AlternateIngredientLine altLine : ingredientLine.alternateLines) {
            query.append("OR searchIngredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND searchIngredient = ?  ) ");
            parameters.add(altLine.ingredient.name);
            parameters.add(altLine.ingredient.name);
          }
        }
        query.append(") ) ) AND ");
      }
    }
    if (searchParameters.withoutIngredients.size() > 0) {
      query.append(" NOT EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND (");
      for (String ingredient : searchParameters.withoutIngredients) {
        query.append("(searchIngredient = ? AND NOT EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id ");
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
      query.append(" pictureName IS NOT NULL AND ");
    }
    if (searchParameters.hasPrepared == 1) {
      query.append(" preparedhistory.userId = ? AND ");
      parameters.add(String.valueOf(searchParameters.userId));
    } else if (searchParameters.hasPrepared == 2) {
      query.append(" preparedhistory.userId IS NULL AND ");
    }
    query.delete(query.length() - 5, query.length());
    if (searchParameters.sortOrder == 0) {
      query.append(" ORDER BY name");
      if (searchParameters.descending) {
        query.append(" DESC");
      }
    } else if (searchParameters.sortOrder == 1) {
      query.append(" ORDER BY username");
      if (searchParameters.descending) {
        query.append(" DESC");
      }
      query.append(", name");
    } else if (searchParameters.sortOrder == 2) {
      query.append(" ORDER BY added");
      if (searchParameters.descending) {
        query.append(" DESC");
      }
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
        List<PreparedHistory> preparedHistoryList = new ArrayList<PreparedHistory>();
        Rating rating = new Rating();
        List<Rating> ratingList = new ArrayList<Rating>();
        result.id = resultSet.getLong("id");
        result.name = resultSet.getString("name");
        result.user = new User();
        result.user.username = resultSet.getString("username");
        result.added = resultSet.getTimestamp("added");
        result.pictureName = resultSet.getString("pictureName");
        preparedHistory.preparedTime = resultSet.getDate("preparedTime");
        preparedHistoryList.add(preparedHistory);
        result.preparedHistory = preparedHistoryList;
        rating.rating = resultSet.getInt("rating");
        ratingList.add(rating);
        result.rating = ratingList;
        result.averageRating = resultSet.getDouble("averageRating");
        return result;
      }
    }, parameters.toArray());
    if (searchParameters.resultsPerPage > 0) {
      result.pages = (template.queryForObject("SELECT FOUND_ROWS();", Long.class) + searchParameters.resultsPerPage - 1) / searchParameters.resultsPerPage;
    } else {
      result.pages = 0;
    }
    return result;
  }
}
