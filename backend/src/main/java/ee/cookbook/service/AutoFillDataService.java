package ee.cookbook.service;

import ee.cookbook.dao.*;
import ee.cookbook.protocol.AutoFillData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AutoFillDataService {
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private IngredientUnitRepository unitRepository;
  @Autowired
  private RecipeSourceRepository sourceRepository;

  public AutoFillData getAutoFillData() {
    AutoFillData data = new AutoFillData();
    data.users = jdbcTemplate.queryForList("SELECT username FROM User", String.class);
    data.categories = categoryRepository.findAll();
    data.units = unitRepository.findAll();
    data.sources = sourceRepository.findAll();
    data.ingredients = jdbcTemplate.query("SELECT searchName, displayName FROM IngredientAutoSuggest", new ResultSetExtractor<Map<String, String>>(){
      @Override
      public Map<String, String> extractData(ResultSet rs) throws SQLException,DataAccessException {
        HashMap<String,String> searchIngredients = new HashMap<String,String>();
        while(rs.next()){
          searchIngredients.put(rs.getString("displayName"),rs.getString("searchName"));
        }
         return searchIngredients;
      }
    });
    data.searchIngredients = jdbcTemplate.queryForList("SELECT DISTINCT searchName FROM IngredientAutoSuggest", String.class);
    return data;
  }

  public List<String> getAutoFillCategories() {
    return categoryRepository.findAll().parallelStream().map(category -> category.name).toList();
  }
}
