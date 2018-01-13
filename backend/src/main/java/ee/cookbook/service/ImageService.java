package ee.cookbook.service;

import ee.cookbook.dao.RecipeRepository;
import ee.cookbook.model.User;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.move;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class ImageService {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private RecipeRepository recipeRepository;

  @Value("${imageFolder}")
  private String imageFolder;

  public ResponseEntity createTemporaryImages(MultipartFile file, Long id, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (id != 0 && recipeRepository.countByIdAndUserId(id, user.id) == 0) {
      throw new IllegalStateException("User " + user.username + " is not allowed to modify recipe with id " + id);
    }
    if (!file.isEmpty()) {
      try {
        String name;
        Tika tika = new Tika();
        byte[] bytes = file.getBytes();
        String[] detected = tika.detect(bytes).split("[/]");
        String mimeType = detected[0];
        String type = detected[1];
        if( !mimeType.equals("image") || !(type.equals("jpg") || type.equals("jpeg") || type.equals("png"))) {
          return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Pildi salvestamine ebaõnnestus, sest saadetud fail ei olnud formaadis png, jpg või jpeg.");
        }
        File dir = new File(imageFolder + "temp");
        if (!dir.exists()) {
          dir.mkdirs();
        }
        // Create the file on server

        if (id > 0) {
          name = "-" + id + "-" + user.id + "." + type;
        } else {
          int i = 0;
          while (exists(Paths.get(imageFolder + "temp/3-" + i + "." + type))) {
            i++;
          }
          name = "-" + i + "." + type;
        }
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
        return ResponseEntity.status(500).body("Pildi salvestamine ebaõnnestus.");
      }
    } else {
      return ResponseEntity.status(400).body("Saadetud fail on tühi.");
    }
  }

  public ResponseEntity saveImages(String name, String extension, Long id) {
    try {
      File dir = new File(imageFolder + id);
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
      move(get(imageFolder + "temp/1" + name + "." + extension), get(imageFolder + id + "/" + 1 + "RecipePicture." + extension), REPLACE_EXISTING);
      move(get(imageFolder + "temp/2" + name + "."  + extension), get(imageFolder + id + "/" + 2 + "RecipePicture." + extension), REPLACE_EXISTING);
      move(get(imageFolder + "temp/3" + name + "." + extension), get(imageFolder + id + "/" + 3 + "RecipePicture." + extension), REPLACE_EXISTING);
      List<String> parameters = new ArrayList<>();
      parameters.add(extension);
      parameters.add(id.toString());
      template.update("UPDATE recipe SET pictureName = ? WHERE id = ?", parameters.toArray());
      return ResponseEntity.ok(extension);
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Pildi salvestamine ebaõnnestus");
    }
  }

  public ResponseEntity deleteSavedImage(Long id) {
    List<Long> parameters = new ArrayList<>();
    parameters.add(id);
    String fileExtension = template.queryForObject("SELECT pictureName FROM recipe WHERE id = ?", parameters.toArray(), String.class);
    try {
      delete(Paths.get(imageFolder + id + "/1RecipePicture." + fileExtension));
      delete(Paths.get(imageFolder + id + "/2RecipePicture." + fileExtension));
      delete(Paths.get(imageFolder + id + "/3RecipePicture." + fileExtension));
      template.update("UPDATE recipe SET pictureName = '' WHERE id = ?", parameters.toArray());
      return ResponseEntity.ok("");
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Pildi kustutamine ebaõnnestus.");
    }
  }

  public ResponseEntity deleteTempImage(String name) {
    try {
      delete(Paths.get(imageFolder + "temp/1" + name));
      delete(Paths.get(imageFolder + "temp/2" + name));
      delete(Paths.get(imageFolder + "temp/3" + name));
      return ResponseEntity.ok("");
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Pildi kustutamine ebaõnnestus.");
    }
  }
}
