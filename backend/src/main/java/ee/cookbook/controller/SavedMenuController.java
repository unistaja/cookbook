package ee.cookbook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ee.cookbook.dao.SavedMenuRepository;
import ee.cookbook.model.RecipeSummary;
import ee.cookbook.model.SavedMenu;
import ee.cookbook.model.User;
import ee.cookbook.service.SavedMenuService;

@RestController
@RequestMapping("/api/menu")
public class SavedMenuController {
  @Autowired
  private SavedMenuRepository menuRepository;
  @Autowired
  private SavedMenuService savedMenuService;

  @RequestMapping(value = "/addRecipe", method = RequestMethod.POST)
  @ResponseBody
  public void addRecipe(@RequestParam Long recipeId, Authentication auth) {
      User user = (User) auth.getPrincipal();
      menuRepository.save(new SavedMenu(user.id, recipeId));
  }

  @RequestMapping(value = "/removeRecipe", method = RequestMethod.POST)
  @ResponseBody
  public void removeRecipe(@RequestParam Long recipeId, Authentication auth) {
      User user = (User) auth.getPrincipal();
      menuRepository.deleteById(new SavedMenu(user.id, recipeId));
  }

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public List<RecipeSummary> getMenuRecipes(Authentication auth) {
      User user = (User) auth.getPrincipal();
      return savedMenuService.getMenuRecipes(user.id);
  }
}
