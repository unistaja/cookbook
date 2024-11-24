package ee.cookbook.controller;

import ee.cookbook.dao.PreparedHistoryRepository;
import ee.cookbook.dao.RatingRepository;
import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.dao.SavedMenuRepository;
import ee.cookbook.model.Category;
import ee.cookbook.model.PreparedHistory;
import ee.cookbook.model.Rating;
import ee.cookbook.model.Recipe;
import ee.cookbook.model.SavedMenu;
import ee.cookbook.model.User;
import ee.cookbook.protocol.AutoFillData;
import ee.cookbook.service.AutoFillDataService;
import ee.cookbook.service.ImageService;
import ee.cookbook.service.ViewHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
  private final static Logger logger = LoggerFactory.getLogger(RecipeController.class);

  @Value("${imageFolder}")
  private String imageFolder;

  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private AutoFillDataService autoFillDataService;
  @Autowired
  private ImageService imageService;
  @Autowired
  private ViewHistoryService viewHistoryService;
  @Autowired
  private PreparedHistoryRepository preparedHistoryRepository;
  @Autowired
  private RatingRepository ratingRepository;
  @Autowired
  private SavedMenuRepository menuRepository;

  @RequestMapping(value = "/{recipeId}", method = RequestMethod.GET)
  Recipe getRecipe(@PathVariable long recipeId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
    viewHistoryService.addRecipeView(user.id, recipeId);
    PreparedHistory preparedHistory = preparedHistoryRepository.findTopByRecipeIdAndUserIdOrderByPreparedTimeDesc(recipeId, user.id);
    if (preparedHistory != null) {
      recipe.preparedHistory = Collections.singletonList(preparedHistory);
    } else {
      recipe.preparedHistory = Collections.emptyList();
    }
    Rating rating = ratingRepository.findByRecipeIdAndUserId(recipeId, user.id);
    if (rating != null) {
      recipe.rating = Collections.singletonList(rating);
    } else {
      recipe.rating = Collections.emptyList();
    }

    return recipe;
  }

  @RequestMapping(value = "/autofill", method = RequestMethod.GET)
  AutoFillData getAutoFillData() {
    return autoFillDataService.getAutoFillData();
  }

  @RequestMapping(value = "/autofill/categories", method = RequestMethod.GET)
  List<String> getCategoriesAutoFillData() {
    return autoFillDataService.getAutoFillCategories();
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<String> add(@RequestBody Recipe recipe, Authentication auth) {
    User user = (User) auth.getPrincipal();
    String imageName = "";
    if (recipe.id != 0 && !user.isAdmin && recipeRepository.countByIdAndUserId(recipe.id, user.id) == 0) {
      logger.error("User {} is not allowed to modify recipe with id {}", user.username, recipe.id);
      throw new IllegalStateException("Teil pole lubatud seda retsepti muuta.");
    }
    Recipe recipeToSave = recipe;
    if (recipe.id != 0) {
      recipeToSave = recipeRepository.findById(recipe.id).orElseThrow();
      recipeToSave.categories.clear();
      recipeToSave.categories.addAll(recipe.categories);
      recipeToSave.ingredientLists.clear();
      recipeToSave.ingredientLists.addAll(recipe.ingredientLists);
      recipeToSave.instructions = recipe.instructions;
      recipeToSave.name = recipe.name;
      recipeToSave.source = recipe.source;
      recipeToSave.amount = recipe.amount;
      recipeToSave.prepareTime = recipe.prepareTime;
    } else {
      recipeToSave.user = user;
    }

    if (!StringUtils.isBlank(recipe.pictureName)) {
      String[] nameParts = recipe.pictureName.split("[.]");
      if (nameParts.length > 1) {
        imageName = nameParts[0];
        recipeToSave.pictureName = nameParts[1];
      } else {
        recipeToSave.pictureName = nameParts[0];
      }
    }
    Recipe savedRecipe = recipeRepository.save(recipeToSave);
    if (!StringUtils.isBlank(imageName)) {
      try {
        imageService.saveImages(imageName, recipeToSave.pictureName, savedRecipe.id);
      } catch(Exception e) {
        logger.error("Saving image to recipe {} failed.", savedRecipe.id, e);
        return ResponseEntity.ok("{\"message\": \"Pildi salvestamine ebaõnnestus, kuid retsept on salvestatud.\", \"recipeId\": " + savedRecipe.id + "}");
      }
    }
    return ResponseEntity.ok("{\"recipeId\": " + savedRecipe.id + "}");
  }

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  List<Recipe> getRecipes() {
    return recipeRepository.findAll();
  }

  @RequestMapping(value = "/savedate", method = RequestMethod.POST)
  ResponseEntity<String> saveDate(@RequestParam("date") String date, @RequestParam("id") Long id, @RequestParam("recipeId") Long recipeId, Authentication auth) {
    PreparedHistory newDate = new PreparedHistory();
    newDate.userId = ((User) auth.getPrincipal()).id;
    try {
      newDate.preparedTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
      if (newDate.preparedTime.after(new Date())) {
        throw new Exception("Prepared time must not be in the future.");
      }
    } catch (Exception e) {
      logger.error("Saving prepared time {} to recipe {} failed. ", date, recipeId, e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valmistuskorda ei saa lisada tulevikku.");
    }
    newDate.recipeId = recipeId;
    if (id > 0) {
      newDate.id = id;
    }
    preparedHistoryRepository.save(newDate);
    try {
      menuRepository.deleteById(new SavedMenu(newDate.userId, recipeId));
    } catch (EmptyResultDataAccessException e) {
      // ignore, recipe was not added to menu
    }
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "/deletedate", method = RequestMethod.POST)
  ResponseEntity<String> deleteDate(@RequestParam("id") Long id, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (preparedHistoryRepository.findById(id).userId == user.id) {
      preparedHistoryRepository.deleteById(id);
    }
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "/saverating", method = RequestMethod.POST)
  ResponseEntity<String> saveRating(@RequestParam("rating") int rating, @RequestParam("recipeId") Long recipeId, Authentication auth) {
    Rating newRating = new Rating();
    newRating.userId = ((User) auth.getPrincipal()).id;
    newRating.rating = rating;
    newRating.recipeId = recipeId;
    ratingRepository.save(newRating);
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "/findpreparedtimes", method = RequestMethod.POST)
  List<PreparedHistory> findPreparedTimes (@RequestParam("recipeId") Long recipeId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    return preparedHistoryRepository.findAllByRecipeIdAndUserIdOrderByPreparedTimeDesc(recipeId, user.id);
  }

  @RequestMapping(value = "/addcategories", method = RequestMethod.POST)
  ResponseEntity<String> saveRating(@RequestParam("recipeId") Long recipeId, @RequestParam("categories") List<String> categories) {
    Recipe recipeToSave = recipeRepository.findById(recipeId).orElseThrow();
    recipeToSave.categories.addAll(categories.parallelStream().map(category -> new Category(category)).toList());
    recipeRepository.save(recipeToSave);
    return ResponseEntity.ok("");
  }
}
