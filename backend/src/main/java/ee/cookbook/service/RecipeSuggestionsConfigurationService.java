package ee.cookbook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ee.cookbook.dao.RecipeSuggestionsConfigurationRepository;
import ee.cookbook.model.RecipeSuggestionsConfiguration;
import ee.cookbook.model.SearchForm;

@Service
public class RecipeSuggestionsConfigurationService {
  @Autowired
  private RecipeSuggestionsConfigurationRepository recipeSuggestionsConfigurationRepository;

  public List<RecipeSuggestionsConfiguration> getUserConfigurations(Long userId) {
    var configs =  recipeSuggestionsConfigurationRepository.findByUserIdOrderById(userId);

    if (configs.isEmpty()) {
      var defaultConfig = createDefaultConfiguration(userId);
      recipeSuggestionsConfigurationRepository.save(defaultConfig);
      configs = List.of(defaultConfig);
    }
    return configs;
  }

  public RecipeSuggestionsConfiguration addConfiguration(RecipeSuggestionsConfiguration config) {
    return recipeSuggestionsConfigurationRepository.save(config);
  }

  private RecipeSuggestionsConfiguration createDefaultConfiguration(Long userId) {
    RecipeSuggestionsConfiguration config = new RecipeSuggestionsConfiguration();
    config.name = "Juhuvalik";
    config.userId = userId;
    config.config = createDefaultSearchForm();
    return config;
    
  }

  private SearchForm createDefaultSearchForm() {
    SearchForm form = new SearchForm();
    form.resultPage = 0;
    form.resultsPerPage = 7;
    form.sortOrder = 4;
    form.withIngredients = List.of();
    form.withoutIngredients = List.of();
    form.categories = List.of();
    return form;
  }
}
