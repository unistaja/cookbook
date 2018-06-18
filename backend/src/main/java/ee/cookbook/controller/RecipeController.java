package ee.cookbook.controller;

import ee.cookbook.dao.PreparedHistoryRepository;
import ee.cookbook.dao.RatingRepository;
import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.*;
import ee.cookbook.protocol.AutoFillData;
import ee.cookbook.service.AutoFillDataService;
import ee.cookbook.service.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
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
  private PreparedHistoryRepository preparedHistoryRepository;
  @Autowired
  private RatingRepository ratingRepository;

  @RequestMapping(value = "/{recipeId}", method = RequestMethod.GET)
  Recipe getRecipe(@PathVariable long recipeId) {
    return recipeRepository.findOne(recipeId);
  }

  @RequestMapping(value = "/autofill", method = RequestMethod.GET)
  AutoFillData getAutoFillData() {
    return autoFillDataService.getAutoFillData();
  }

  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity add(@RequestBody Recipe recipe, Authentication auth) {
    User user = (User) auth.getPrincipal();
    String imageName = "";
    if (recipe.id != 0 && recipeRepository.countByIdAndUserId(recipe.id, user.id) == 0) {
      logger.error("User " + user.username + " is not allowed to modify recipe with id " + recipe.id);
      throw new IllegalStateException("Teil pole lubatud seda retsepti muuta.");
    }
    recipe.added = new Date(System.currentTimeMillis() + 1000*60*60*3);
    if (!StringUtils.isBlank(recipe.pictureName)) {
      String[] nameParts = recipe.pictureName.split("[.]");
      if (nameParts.length > 1) {
        imageName = nameParts[0];
        recipe.pictureName = nameParts[1];
      } else {
        recipe.pictureName = nameParts[0];
      }
    }
    recipe.user = user;
    Recipe savedRecipe = recipeRepository.save(recipe);
    if (!StringUtils.isBlank(imageName)) {
      try {
        imageService.saveImages(imageName, recipe.pictureName, savedRecipe.id);
      } catch(Exception e) {
        logger.error("Saving image to recipe {} failed.", savedRecipe.id, e);
        return ResponseEntity.status(500).body("{\"message\": \"Pildi salvestamine ebaõnnestus, kuid retsept on salvestatud.\"}");
      }
    }
    return ResponseEntity.ok("{\"recipeId\": " + savedRecipe.id + "}");
  }

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  List<Recipe> getRecipes() {
    return recipeRepository.findAll();
  }

  @RequestMapping(value = "/savedate", method = RequestMethod.POST)
  ResponseEntity saveDate(@RequestParam("date") String date, @RequestParam("id") Long id, @RequestParam("recipeId") Long recipeId, Authentication auth) {
    PreparedHistory newDate = new PreparedHistory();
    newDate.userId = ((User) auth.getPrincipal()).id;
    try {
      newDate.preparedTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    } catch (Exception e) {}
    newDate.recipeId = recipeId;
    if (id > 0) {
      newDate.id = id;
    }
    preparedHistoryRepository.save(newDate);
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "/saverating", method = RequestMethod.POST)
  ResponseEntity saveRating(@RequestParam("rating") int rating, @RequestParam("id") Long id, @RequestParam("recipeId") Long recipeId, Authentication auth) {
    Rating newRating = new Rating();
    newRating.userId = ((User) auth.getPrincipal()).id;
    newRating.rating = rating;
    newRating.recipeId = recipeId;
    if (id > 0) {
     newRating.id = id;
    }
    ratingRepository.save(newRating);
    return ResponseEntity.ok("");
  }
}
