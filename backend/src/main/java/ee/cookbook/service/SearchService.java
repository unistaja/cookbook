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
    query.append("SELECT SQL_CALC_FOUND_ROWS recipe.id, username, recipe.name, added, pictureName FROM recipe JOIN user ON recipe.userId = user.id WHERE");
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
        query.append(" EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND  (ingredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND ingredient = ?  ) ");
        parameters.add(ingredientLine.ingredient.name);
        parameters.add(ingredientLine.ingredient.name);
        if (ingredientLine.alternateLines.size() > 0) {
          for (AlternateIngredientLine altLine : ingredientLine.alternateLines) {
            query.append("OR ingredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND ingredient = ?  ) ");
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
        query.append("(ingredient = ? AND NOT EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id ");
        parameters.add(ingredient);
        for (String ingr : searchParameters.withoutIngredients) {
          query.append("AND NOT ingredient = ? ");
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
        result.id = resultSet.getLong("id");
        result.name = resultSet.getString("name");
        result.user = new User();
        result.user.username = resultSet.getString("username");
        result.added = resultSet.getTimestamp("added");
        result.pictureName = resultSet.getString("pictureName");
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