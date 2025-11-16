package ee.cookbook.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
public class RecipeSuggestionsConfiguration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public Long userId;

  public String name;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "JSON")
  public SearchForm config;
  
}
