package ee.cookbook.model;

import java.util.List;

public class SearchForm {
  public Boolean hasPicture;

  public String username;

  public RecipeSource source;

  public String name;

  public Boolean exactName = false;

  public List<IngredientLine> withIngredients;

  public List<String> withoutIngredients;

  public List<String> categories;

  public int sortOrder = 0;

  public Boolean descending = false;

  public int resultsPerPage;

  public long resultPage;

  public Boolean hasPrepared = null;
}
