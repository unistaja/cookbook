package ee.cookbook.service;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.apache.tika.exception.UnsupportedFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.Files.delete;
import static java.nio.file.Files.move;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.UUID.randomUUID;

@Service
public class ImageService {

  private final static Logger logger = LoggerFactory.getLogger(ImageService.class);

  private Tika tika = new Tika();

  @Autowired
  private JdbcTemplate template;

  private final String imageFolder;
  ImageService(@Value("${imageFolder}") String imageFolder) {
    this.imageFolder = imageFolder;
    File dir = new File(imageFolder + "temp");
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        logger.error("Creating new image folder ({}) failed.", dir.getAbsolutePath());
      }
    }
  }
  public String createTemporaryImages(MultipartFile file, Long recipeId, Long userId) throws IllegalArgumentException, IOException, UnsupportedFormatException {
    if (!file.isEmpty()) {
      String name;
      byte[] bytes = file.getBytes();
      String type = getImageType(bytes);

      // Create the file on server

      if (recipeId != null) {
        name = "-" + recipeId + "-" + userId + "." + type;
      } else {
        name = "-" + randomUUID() + "." + type;
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

      return name;
    } else {
      throw new IllegalArgumentException("Saadetud fail on tühi");
    }
  }

  public String saveImages(String name, String extension, Long recipeId) throws IOException, DataAccessException {
    prepareImageDirectory(recipeId);
    for (int i = 1; i < 4; i++) {
      move(get(imageFolder + "temp/" + i + name + "." + extension), get(imageFolder + recipeId + "/" + i + "RecipePicture." + extension), REPLACE_EXISTING);
    }
    template.update("UPDATE Recipe SET pictureName = ? WHERE id = ?", extension, recipeId);
    return extension;
  }

  public void deleteSavedImage(Long recipeId) throws DataAccessException {
    String fileExtension = template.queryForObject("SELECT pictureName FROM recipe WHERE id = ?", String.class, recipeId);
    try {
      template.update("UPDATE Recipe SET pictureName = '' WHERE id = ?", recipeId);
      delete(Paths.get(imageFolder + recipeId + "/1RecipePicture." + fileExtension));
      delete(Paths.get(imageFolder + recipeId + "/2RecipePicture." + fileExtension));
      delete(Paths.get(imageFolder + recipeId + "/3RecipePicture." + fileExtension));
    } catch (IOException e) {
      logger.error("Deleting images from folder {}{} failed. ", imageFolder, recipeId, e);
    }
  }

  public void deleteTempImage(String name) throws IOException {
    delete(Paths.get(imageFolder + "temp/1" + name));
    delete(Paths.get(imageFolder + "temp/2" + name));
    delete(Paths.get(imageFolder + "temp/3" + name));
  }

  private String getImageType(byte[] bytes) throws UnsupportedFormatException {
    String[] detected = tika.detect(bytes).split("[/]");
    String mimeType = detected[0];
    String type = detected[1];
    if( !mimeType.equals("image") || !(type.equals("jpg") || type.equals("jpeg") || type.equals("png"))) {
      throw new UnsupportedFormatException("Pildifail peab olema jpg, jpeg või png tüüpi.");
    }
    return type;
  }

  private void prepareImageDirectory(Long recipeId) throws IOException {
    File dir = new File(imageFolder + recipeId);
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        logger.error("Creating image folder for recipe {} failed.", recipeId);
        throw new IOException();
      }
    } else {
      File[] images = dir.listFiles();
      if (images != null) {
        for(File f: images) {
          if (!f.delete()) {
            logger.error("Deleting existing images from {} failed.", dir.getAbsolutePath());
          }
        }
      }
    }
  }
}

