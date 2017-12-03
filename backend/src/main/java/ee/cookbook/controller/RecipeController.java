package ee.cookbook.controller;

import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.*;
import ee.cookbook.protocol.AutoFillData;
import ee.cookbook.service.AutoFillDataService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.move;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
  @Value("${imageFolder}")
  private String imageFolder;

  @Autowired
  private RecipeRepository recipeRepository;
  @Autowired
  private AutoFillDataService autoFillDataService;
  @Autowired
  private JdbcTemplate template;

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
    String imageName = "";
    if (recipe.id != 0 && recipeRepository.countByIdAndUserId(recipe.id, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + recipe.id);
    }
    recipe.added = new Date(System.currentTimeMillis() + 10800000);
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
        File dir = new File(imageFolder + savedRecipe.id);

        if (!dir.exists()) {
          dir.mkdirs();
        } else {
          File[] images = dir.listFiles();
          if (images != null) {
            for(File f: images) {
              f.delete();
            }
          }
        }
        move(get(imageFolder + "temp/1" + imageName + "." + recipe.pictureName), get(imageFolder + savedRecipe.id + "/" + 1 + "RecipePicture." + recipe.pictureName), REPLACE_EXISTING);
        move(get(imageFolder + "temp/2" + imageName + "."  + recipe.pictureName), get(imageFolder + savedRecipe.id + "/" + 2 + "RecipePicture." + recipe.pictureName), REPLACE_EXISTING);
        move(get(imageFolder + "temp/3" + imageName + "." + recipe.pictureName), get(imageFolder + savedRecipe.id + "/" + 3 + "RecipePicture." + recipe.pictureName), REPLACE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return "{\"recipeId\": " + savedRecipe.id + "}";

  }

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  List<Recipe> getRecipes() {
    return recipeRepository.findAll();
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public List<Object> searchRecipes(@RequestBody SearchForm recipe) {
    List<String> parameters = new ArrayList<>();
    StringBuilder query = new StringBuilder();
    query.append("SELECT SQL_CALC_FOUND_ROWS recipe.id, username, recipe.name, added, pictureName FROM recipe JOIN user ON recipe.userId = user.id WHERE");
    if (!StringUtils.isBlank(recipe.name)) {
      query.append(" name = ? AND ");
      parameters.add(recipe.name);
    }
    if (!StringUtils.isBlank(recipe.username)) {
      query.append(" username = ? AND ");
      parameters.add(recipe.username);
    }
    if (recipe.source != null && !StringUtils.isBlank(recipe.source.name)) {
      query.append(" source = ? AND ");
      parameters.add(recipe.source.name);
    }
    if (recipe.categories.size() > 0) {
      for (String category : recipe.categories) {
        query.append(" EXISTS (SELECT * FROM recipecategory WHERE recipeId = recipe.id AND category = ? ) AND ");
        parameters.add(category);
      }
    }
    if (recipe.withIngredients.size() > 0) {
      query.append(" EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND ");
      for (IngredientLine ingredientLine : recipe.withIngredients) {
        query.append("EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND  (ingredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND ingredient = ?  ) ");
        parameters.add(ingredientLine.ingredient.name);
        parameters.add(ingredientLine.ingredient.name);
        if (ingredientLine.alternateLines.size() > 0) {
          for (AlternateIngredientLine altLine : ingredientLine.alternateLines) {
            query.append("OR ingredient = ? OR EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id AND ingredient = ?  ) ");
            parameters.add(altLine.ingredient.name);
            parameters.add(altLine.ingredient.name);
          }
        }
        query.append(") ) AND ");
      }
      query.delete(query.length() - 4, query.length());
      query.append(") AND ");
    }
    if (recipe.withoutIngredients.size() > 0) {
      query.append(" NOT EXISTS (SELECT * FROM ingredientlist WHERE recipeId = recipe.id AND EXISTS (SELECT * FROM ingredientline WHERE listId = ingredientlist.id AND (");
      for (String ingredient : recipe.withoutIngredients) {
        query.append("(ingredient = ? AND NOT EXISTS (SELECT * FROM alternateingredientline WHERE lineId = ingredientline.id ");
        parameters.add(ingredient);
        for (String ingr : recipe.withoutIngredients) {
          query.append("AND NOT ingredient = ? ");
          parameters.add(ingr);
        }
        query.append(") ) OR ");
      }
      query.delete(query.length() - 3, query.length());
      query.append(") ) ) AND ");
    }
    if (recipe.hasPicture) {
      query.append(" pictureName IS NOT NULL AND ");
    }
    query.delete(query.length() - 5, query.length());
    if (recipe.sortOrder == 0) {
      query.append(" ORDER BY name");
      if (recipe.descending) {
        query.append(" DESC");
      }
    } else if (recipe.sortOrder == 1) {
      query.append(" ORDER BY username");
      if (recipe.descending) {
        query.append(" DESC");
      }
      query.append(", name");
    } else if (recipe.sortOrder == 2) {
      query.append(" ORDER BY added");
      if (recipe.descending) {
        query.append(" DESC");
      }
    }

    if (recipe.resultsPerPage > 0) {
      query.append(" LIMIT ");
      query.append(recipe.resultPage * recipe.resultsPerPage);
      query.append(", ");
      query.append(recipe.resultsPerPage);
    }
    String sql = query.toString();

    List<Object> result = new ArrayList<>();
      List<Recipe> recipes = template.query(sql, new RowMapper<Recipe>() {
        @Override
        public Recipe mapRow(ResultSet resultSet, int i) throws SQLException {
          Recipe result = new Recipe();
          result.id = resultSet.getLong("id");
          result.name = resultSet.getString("name");
          result.user = new User();
          result.user.username = resultSet.getString("username");
          result.added = resultSet.getTimestamp("added");
          result.pictureName = resultSet.getString("pictureName");
          return result;
        }
      }, parameters.toArray());
      List<Long> maxResults = template.queryForList("SELECT FOUND_ROWS();", Long.class);
    result.add(recipes);
    if (recipe.resultsPerPage > 0) {
      result.add((maxResults.get(0) + recipe.resultsPerPage - 1) / recipe.resultsPerPage);
    } else {
      result.add(0);
    }
    return result;
  }
  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity image(@RequestParam("file") MultipartFile file, @RequestParam("name") String name, Authentication auth) {
    if (!file.isEmpty()) {
        try {
          Tika tika = new Tika();
          byte[] bytes = file.getBytes();
          String[] detected = tika.detect(bytes).split("[/]");
          String mimeType = detected[0];
          String type = detected[1];
          if( !mimeType.equals("image") || !(type.equals("jpg") || type.equals("jpeg") || type.equals("png"))) {
            throw new Exception("Üleslaetud fail ei ole pilt formaadiga jpg, jpeg või png.");
          }
          File dir = new File(imageFolder + "temp");
          if (!dir.exists()) {
            dir.mkdirs();
          }
          // Create the file on server
          int i = 0;
          while (exists(Paths.get(imageFolder + "temp/3-" + i + "." + type))) {
            i++;
          }
          name = "-" + i + "." + type;
          File serverFile = new File(imageFolder + "temp/3" + name);
          BufferedOutputStream stream = new BufferedOutputStream(
                  new FileOutputStream(serverFile));
          stream.write(bytes);
          stream.close();
          BufferedImage shownImage =
                  Thumbnails.of(serverFile)
                          .width(350)
                          .asBufferedImage();

          ImageIO.write(shownImage, type, new File(imageFolder + "temp/1" + name));
          BufferedImage thumbnail =
                  Thumbnails.of(serverFile)
                          .height(100)
                          .asBufferedImage();
          ImageIO.write(thumbnail, type, new File(imageFolder + "temp/2" + name));

          return ResponseEntity.ok(name);
        } catch (Exception e) {
          ResponseEntity.status(415);
          return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Pildi salvestamine ebaõnnestus => " + e.getMessage());
        }
      } else {
        return ResponseEntity.status(400).body("Saadetud fail on tühi.");
      }
  }
  @RequestMapping(value = "/deleteimage", method = RequestMethod.POST)
  void deleteImage(@RequestBody String[] info) {
    try {
      if(!info[0].equals("temp")) {
        List<String> parameters = new ArrayList<>();
        parameters.add(info[0]);
        template.update("UPDATE recipe SET pictureName = '' WHERE id = ?", parameters.toArray());
        delete(Paths.get(imageFolder + info[0]+ "/1RecipePicture." + info[1]));
        delete(Paths.get(imageFolder + info[0]+ "/2RecipePicture." + info[1]));
        delete(Paths.get(imageFolder + info[0]+ "/3RecipePicture." + info[1]));
        return;
      }
      delete(Paths.get(imageFolder + info[0]+ "/1" + info[1]));
      delete(Paths.get(imageFolder + info[0]+ "/2" + info[1]));
      delete(Paths.get(imageFolder + info[0]+ "/3" + info[1]));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  @RequestMapping(value = "/saveimage", method = RequestMethod.POST)
  void saveImage(@RequestBody String[] info) {
    String[] nameParts = info[0].split("[.]");
    File dir = new File(imageFolder + info[1]);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String fileExtension = nameParts[1];
    try {
      move(get(imageFolder + "temp/1" + info[0]), get(imageFolder + info[1] + "/" + 1 + "RecipePicture." + fileExtension), REPLACE_EXISTING);
      move(get(imageFolder + "temp/2" + info[0]), get(imageFolder + info[1] + "/" + 2 + "RecipePicture." + fileExtension), REPLACE_EXISTING);
      move(get(imageFolder + "temp/3" + info[0]), get(imageFolder + info[1] + "/" + 3 + "RecipePicture." + fileExtension), REPLACE_EXISTING);
      List<String> parameters = new ArrayList<>();
      parameters.add(fileExtension);
      parameters.add(info[1]);
      template.update("UPDATE recipe SET pictureName = ? WHERE id = ?", parameters.toArray());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
