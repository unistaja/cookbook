package ee.cookbook.schedule;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class SchedulePeriodicImageDeletion {
  @Value("${imageFolder}")
  private String imageFolder;
  @Scheduled(cron = "0 * 12 * * ?")
  public void deleteImages() {
    System.out.println("SCHEDULE!");
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
