package ee.cookbook.controller;

import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.Recipe;
import ee.cookbook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
  @Autowired
  RecipeRepository recipeRepository;

  @RequestMapping(value = "/{recipeId}", method = RequestMethod.GET)
  Recipe getRecipe(@PathVariable long recipeId) {
    return recipeRepository.findOne(recipeId);
  }

  @RequestMapping(method = RequestMethod.POST)
  String add(@RequestBody Recipe recipe, Authentication auth) {
    recipe.user = (User) auth.getPrincipal();
    recipeRepository.save(recipe);
    return "Recieved recipe with name " + recipe.name;

  }

}
