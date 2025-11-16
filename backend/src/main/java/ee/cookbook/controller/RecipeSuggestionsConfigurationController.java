package ee.cookbook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ee.cookbook.model.RecipeSuggestionsConfiguration;
import ee.cookbook.model.User;
import ee.cookbook.service.RecipeSuggestionsConfigurationService;

@RestController
@RequestMapping("/api/recipeSuggestionsConfiguration")
public class RecipeSuggestionsConfigurationController {
  @Autowired
  private RecipeSuggestionsConfigurationService recipeSuggestionsConfigurationService;

  @RequestMapping(path = "/getUserConfigurations", method = RequestMethod.GET)
  public List<RecipeSuggestionsConfiguration> getUserConfigurations(Authentication auth) {
    User user = (User) auth.getPrincipal();
    return recipeSuggestionsConfigurationService.getUserConfigurations(user.id);
  }

  @RequestMapping(path = "/addConfiguration", method = RequestMethod.POST)
  public RecipeSuggestionsConfiguration addConfiguration(Authentication auth, @RequestBody RecipeSuggestionsConfiguration config) {
    User user = (User) auth.getPrincipal();
    config.userId = user.id;
    return recipeSuggestionsConfigurationService.addConfiguration(config);
  }
}

