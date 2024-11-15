package ee.cookbook.controller;

import ee.cookbook.model.*;
import ee.cookbook.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {
  @Autowired
  private SearchService searchService;
  @RequestMapping(method = RequestMethod.POST)
  public SearchResult search(@RequestBody SearchForm searchParameters) {
    return searchService.findRecipes(searchParameters);
  }
}
