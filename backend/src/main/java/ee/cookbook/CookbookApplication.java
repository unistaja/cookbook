package ee.cookbook;

import ee.cookbook.schedule.deleteImages;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


@SpringBootApplication
public class CookbookApplication {
	public static void main(String[] args) throws SchedulerException {
		SpringApplication.run(CookbookApplication.class, args);
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		JobDetail job = newJob(deleteImages.class)
						.withIdentity("deleteImages")
						.build();
		Trigger trigger = newTrigger()
						.withIdentity("myTrigger")
						.withSchedule(cronSchedule("0 0 3 * * ?"))
						.build();
		sched.scheduleJob(job, trigger);
		sched.start();
	}
}
