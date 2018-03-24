package ee.cookbook.controller;

import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.User;
import org.apache.tika.exception.UnsupportedFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ee.cookbook.service.ImageService;
import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class ImageController {

  private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

  @Value("${imageFolder}")
  private String imageFolder;

  @Autowired
  private ImageService imageService;

  @Autowired
  private RecipeRepository recipeRepository;

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public ResponseEntity image(@RequestParam("file") MultipartFile file, @Param("id") Long id, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (id != null && recipeRepository.countByIdAndUserId(id, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + id);
    }
    try {
      return ResponseEntity.ok(imageService.createTemporaryImages(file, id, user.id));
    } catch(IOException e) {
      logger.error("Creating temporary image failed. ", e);
      return ResponseEntity.status(500).body("Pildi salvestamine ebaõnnestus");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(400).body(e.getMessage());
    } catch (UnsupportedFormatException e) {
      logger.error("Creating temporary image failed. ", e);
      return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
    }
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  ResponseEntity saveImage(@RequestParam("id") Long recipeId, @RequestParam("extension") String fileExtension, Authentication auth) {
    User user = (User) auth.getPrincipal();
    String name = "-" + recipeId + "-" + user.id;
    try {
      return ResponseEntity.ok(imageService.saveImages(name, fileExtension, recipeId));
    } catch (IOException e) {
      logger.error("Saving image to folder {}{} failed. ", imageFolder, recipeId, e);
      return ResponseEntity.status(500).body("Pildi salvestamine ebaõnnestus");
    } catch (DataAccessException e) {
      logger.error("Saving recipe {} image information to database failed. ", recipeId, e);
      return ResponseEntity.status(500).body("Pildi salvestamine ebaõnnestus.");
    }
  }

  @RequestMapping(value = "/deletetempimage", method = RequestMethod.POST)
  ResponseEntity deleteTempImage(@RequestParam("name") String name) {
    try {
      imageService.deleteTempImage(name);
      return ResponseEntity.ok("");
    } catch (IOException e) {
      logger.error("Deleting images from temp folder failed. ", e);
      return ResponseEntity.status(500).body("Pildi kustutamine ebaõnnestus.");
    }
  }
  @RequestMapping(value = "/deletesavedimage", method = RequestMethod.POST)
  ResponseEntity deleteSavedImage(@RequestParam("id") Long recipeId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (recipeRepository.countByIdAndUserId(recipeId, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + recipeId);
    }
    try {
      imageService.deleteSavedImage(recipeId);
      return ResponseEntity.ok("");
    } catch (DataAccessException e) {
      logger.error("Deleting recipe {} image information from database failed. ", recipeId, e);
      return ResponseEntity.status(500).body("Pildi kustutamine ebaõnnestus.");
    }

  }
}
