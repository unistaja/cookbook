package ee.cookbook.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ee.cookbook.model.RecipeSuggestionsConfiguration;

public interface RecipeSuggestionsConfigurationRepository extends JpaRepository<RecipeSuggestionsConfiguration, Long> {
  public List<RecipeSuggestionsConfiguration> findByUserIdOrderById(Long userId);
} 
