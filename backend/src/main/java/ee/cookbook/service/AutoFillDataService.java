package ee.cookbook.service;

import ee.cookbook.dao.*;
import ee.cookbook.protocol.AutoFillData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutoFillDataService {
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private IngredientListNameRepository ingredientListNameRepository;
  @Autowired
  private IngredientRepository ingredientRepository;
  @Autowired
  private IngredientUnitRepository unitRepository;
  @Autowired
  private RecipeSourceRepository sourceRepository;

  public AutoFillData getAutoFillData() {
    AutoFillData data = new AutoFillData();
    data.categories = categoryRepository.findAll();
    data.ingredients = ingredientRepository.findAll();
    data.units = unitRepository.findAll();
    data.listNames = ingredientListNameRepository.findAll();
    data.sources = sourceRepository.findAll();
    return data;
  }
}
