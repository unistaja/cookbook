package ee.cookbook.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;

public class deleteImages implements Job {
  public void deleteOldImages() {
  }

  public void execute(JobExecutionContext context)
          throws JobExecutionException {

    JobDataMap data = context.getMergedJobDataMap();
    final File dir = new File("backend/src/main/resources/images/temp");
    if (dir.exists()) {
      final File[] images = dir.listFiles();
      for (File image:images) {
        if (image.lastModified() < System.currentTimeMillis() - 86400000) {
          image.delete();
        }
      }
    }
  }
}
