package ee.cookbook;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class CookbookApplication {
	public static void main(String[] args) {
		SpringApplication.run(CookbookApplication.class, args);
	}
}
