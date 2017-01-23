package ee.cookbook.controller;

import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.Recipe;
import ee.cookbook.model.User;
import ee.cookbook.protocol.AutoFillData;
import ee.cookbook.service.AutoFillDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private AutoFillDataService autoFillDataService;

  @RequestMapping(value = "/{recipeId}", method = RequestMethod.GET)
  Recipe getRecipe(@PathVariable long recipeId) {
    return recipeRepository.findOne(recipeId);
  }

  @RequestMapping(value = "/autofill", method = RequestMethod.GET)
  AutoFillData getAutoFillData() {
    return autoFillDataService.getAutoFillData();
  }

  @RequestMapping(method = RequestMethod.POST)
  String add(@RequestBody Recipe recipe, Authentication auth) {
    User user = (User) auth.getPrincipal();

    if (recipe.id != 0 && recipeRepository.countByIdAndUserId(recipe.id, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + recipe.id);
    }

    recipe.user = user;

    Recipe savedRecipe = recipeRepository.save(recipe);
    return "{\"recipeId\": " + savedRecipe.id + "}";

  }

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  List<Recipe> getRecipes() {
    return recipeRepository.findAll();
  }

}
