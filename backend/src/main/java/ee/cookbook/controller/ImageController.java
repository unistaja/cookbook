package ee.cookbook.controller;

import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ee.cookbook.service.ImageService;

@RestController
@RequestMapping("/api/image")
public class ImageController {
  @Value("${imageFolder}")
  private String imageFolder;

  @Autowired
  private ImageService imageService;

  @Autowired
  private RecipeRepository recipeRepository;

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public ResponseEntity image(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id, Authentication auth) {
    return imageService.createTemporaryImages(file, id, auth);
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  ResponseEntity saveImage(@RequestParam("id") Long recipeId, @RequestParam("extension") String fileExtension, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (recipeId != 0 && recipeRepository.countByIdAndUserId(recipeId, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + recipeId);
    }
    String name = "-" + recipeId + "-" + user.id;
    return imageService.saveImages(name, fileExtension, recipeId);
  }

  @RequestMapping(value = "/deletetempimage", method = RequestMethod.POST)
  ResponseEntity deleteTempImage(@RequestParam("name") String name) {
    return imageService.deleteTempImage(name);
  }
  @RequestMapping(value = "/deletesavedimage", method = RequestMethod.POST)
  ResponseEntity deleteSavedImage(@RequestParam("id") Long id, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (recipeRepository.countByIdAndUserId(id, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + id);
    }
    return imageService.deleteSavedImage(id);
  }
}
