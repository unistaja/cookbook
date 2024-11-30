package ee.cookbook.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MenuPlannerConfiguration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;

  List<List<String>> categoriesToInclude;

  List<String> categoriesToExclude;

  List<List<String>> ingredientsToInclude;

  List<String> ingredientsToExclude;

  String source;

  Boolean hasPrepared;

  Boolean onlyMine;


  
}
