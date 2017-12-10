package ee.cookbook.schedule;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class SchedulePeriodicImageDeletion {
  @Value("${imageFolder}")
  private String imageFolder;
  @Scheduled(cron = "0 0 3 * * ?")
  public void deleteImages() {
    final File dir = new File(imageFolder + "temp/");
    if (dir.exists()) {
      final File[] images = dir.listFiles();
      for (File image:images) {
        if (image.lastModified() < System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
          image.delete();
        }
      }
    }
  }
}
