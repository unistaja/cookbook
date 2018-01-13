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

  public List<Object> searchRecipes(SearchForm recipe) {
    List<String> parameters = new ArrayList<>();
    StringBuilder query = new StringBuilder();
    query.append("SELECT SQL_CALC_FOUND_ROWS recipe.id, username, recipe.name, added, pictureName FROM recipe JOIN user ON recipe.userId = user.id WHERE");
    if (!StringUtils.isBlank(recipe.name)) {
      query.append(" name = ? AND ");
      parameters.add(recipe.name);
    }
    if (!StringUtils.isBlank(recipe.username)) {
      query.append(" username = ? AND ");
      parameters.add(recipe.username);
    }
    if (recipe.source != null && !StringUtils.isBlank(recipe.source.name)) {
      query.append(" source = ? AND ");
      parameters.add(recipe.source.name);
    }
    if (recipe.categories.size() > 0) {
      for (String category : recipe.categories) {
        query.append(" EXISTS (SELECT * FROM recipecategory WHERE recipeId = recipe.id AND category = ? ) AND ");
        parameters.add(category);
      }
    }
    if (recipe.withIngredients.size() > 0) {
      query.append(" EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND ");
      for (IngredientLine ingredientLine : recipe.withIngredients) {
        query.append("EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND  (ingredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND ingredient = ?  ) ");
        parameters.add(ingredientLine.ingredient.name);
        parameters.add(ingredientLine.ingredient.name);
        if (ingredientLine.alternateLines.size() > 0) {
          for (AlternateIngredientLine altLine : ingredientLine.alternateLines) {
            query.append("OR ingredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND ingredient = ?  ) ");
            parameters.add(altLine.ingredient.name);
            parameters.add(altLine.ingredient.name);
          }
        }
        query.append(") ) AND ");
      }
      query.delete(query.length() - 4, query.length());
      query.append(") AND ");
    }
    if (recipe.withoutIngredients.size() > 0) {
      query.append(" NOT EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND (");
      for (String ingredient : recipe.withoutIngredients) {
        query.append("(ingredient = ? AND NOT EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id ");
        parameters.add(ingredient);
        for (String ingr : recipe.withoutIngredients) {
          query.append("AND NOT ingredient = ? ");
          parameters.add(ingr);
        }
        query.append(") ) OR ");
      }
      query.delete(query.length() - 3, query.length());
      query.append(") ) ) AND ");
    }
    if (recipe.hasPicture) {
      query.append(" pictureName IS NOT NULL AND ");
    }
    query.delete(query.length() - 5, query.length());
    if (recipe.sortOrder == 0) {
      query.append(" ORDER BY name");
      if (recipe.descending) {
        query.append(" DESC");
      }
    } else if (recipe.sortOrder == 1) {
      query.append(" ORDER BY username");
      if (recipe.descending) {
        query.append(" DESC");
      }
      query.append(", name");
    } else if (recipe.sortOrder == 2) {
      query.append(" ORDER BY added");
      if (recipe.descending) {
        query.append(" DESC");
      }
    }

    if (recipe.resultsPerPage > 0) {
      query.append(" LIMIT ");
      query.append(recipe.resultPage * recipe.resultsPerPage);
      query.append(", ");
      query.append(recipe.resultsPerPage);
    }
    String sql = query.toString();

    List<Object> result = new ArrayList<>();
    List<Recipe> recipes = template.query(sql, new RowMapper<Recipe>() {
      @Override
      public Recipe mapRow(ResultSet resultSet, int i) throws SQLException {
        Recipe result = new Recipe();
        result.id = resultSet.getLong("id");
        result.name = resultSet.getString("name");
        result.user = new User();
        result.user.username = resultSet.getString("username");
        result.added = resultSet.getTimestamp("added");
        result.pictureName = resultSet.getString("pictureName");
        return result;
      }
    }, parameters.toArray());
    List<Long> maxResults = template.queryForList("SELECT FOUND_ROWS();", Long.class);
    result.add(recipes);
    if (recipe.resultsPerPage > 0) {
      result.add((maxResults.get(0) + recipe.resultsPerPage - 1) / recipe.resultsPerPage);
    } else {
      result.add(0);
    }
    return result;
  }
}
