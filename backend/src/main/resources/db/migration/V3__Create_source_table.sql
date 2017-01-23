CREATE TABLE RecipeSource (
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY(name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Recipe ADD INDEX Recipe_Source_FKI(source);

INSERT INTO RecipeSource (name) SELECT DISTINCT source FROM Recipe WHERE source IS NOT NULL;

ALTER TABLE Recipe ADD CONSTRAINT Recipe_Source_FK FOREIGN KEY (source) REFERENCES RecipeSource(name);