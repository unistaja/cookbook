import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ee.cookbook.CookbookApplication;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import java.io.File;
import java.sql.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        FlywayTestExecutionListener.class })
@SpringApplicationConfiguration(classes = CookbookApplication.class)
@WebAppConfiguration
@IntegrationTest
@TestPropertySource(locations= "classpath:application-test.properties")
@FlywayTest(invokeCleanDB = true)



public abstract class BaseSelenideTest {
  @BeforeClass
  public static void browserSetUp() {
    if (System.getProperty("os.name").contains("Windows")) {
      System.setProperty("webdriver.chrome.driver", "./chromedriver/chromedriver.exe");
    } else if (System.getProperty("os.name").contains("Mac")) {
      System.setProperty("webdriver.chrome.driver", "./chromedriver/chromedrivermac");
    } else {
      if ( "x86".equals(System.getProperty("os.arch"))) {
        System.setProperty("webdriver.chrome.driver", "./chromedriver/chromedriverlinux32");
      } else if ( "x86_64".equals(System.getProperty("os.arch"))) {
        System.setProperty("webdriver.chrome.driver", "./chromedriver/chromedriverlinux64");
      }
    }
    Configuration.browser = "chrome";
    Configuration.browserSize = "1920x1080";
    Configuration.holdBrowserOpen = false;
    Configuration.timeout = 1500;
  }

  public String baseUrl;
  //account details
  public final String testUsername = "Selenide";
  public String testPassword = "test";
  //creating a user account
  @Value ("${flyway.url}")
  public String flywayUrl;

  @Value ("${flyway.user}")
  public String flywayUser;

  @Value ("${flyway.password}")
  public String flywayPassword;

  @Value("${imageFolder}")
  public String imageFolder;

  @Before
  public void setUpTest() {
    try {
      Connection conn = DriverManager.getConnection(flywayUrl, flywayUser, flywayPassword);
      String query = "INSERT INTO `cookbooktest`.`user` (`username`, `passwordHash`, `active`) VALUES ('Selenide', '$2a$10$gjZVQu8KKAAsWA3OafjGFOxoD/3kjOr7a2zhJsZnHf.Gs29Tsdp.W', 1);";
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.execute();
      conn.close();
    } catch (Exception e) {
      System.err.println("Got an exception! ");
      System.err.println(e.getMessage());
    }
    open("/login");
    login();
    baseUrl = url().substring(0, url().length()-2);
  }

  //logging in
  public void login() {
    $(By.name("username")).setValue(testUsername);
    $(By.name("password")).setValue(testPassword);
    $(By.name("submit")).click();
  }

  @After
  public void closePage() {
    close();
  }

  @After
  public void deleteTestImageFolder() {
    try {
      FileUtils.deleteDirectory(new File(imageFolder));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public SelenideElement selectByPlaceholder(String placeholderValue) {
    return $(Selectors.byAttribute("placeholder", placeholderValue));
  }

  //going to a page
  public void openRecipeList() {
    $(Selectors.byText("Retseptid")).scrollTo();
    $(Selectors.byText("Retseptid")).click();
  }

  public void openAddRecipePage() {
    $(Selectors.byText("Lisa retsept")).click();
  }

  public void openUserPage() {
    $(By.className("logout")).click();
  }

  public void openSearchPage() {
    $(Selectors.byText("Otsing")).click();
  }

  public SelenideElement getTitleField() {
    return $(By.id("title"));
  }

  public void addAltLine(int list, int line) {
    $(By.id("list"+list+"-line"+line+"-addAlt")).click();
  }

  public void addLine(int list) {
    $(By.id("list"+list+"-addLine")).should(exist);
    $(By.id("list"+list+"-addLine")).scrollTo();
    $(By.id("list"+list+"-addLine")).click();
  }

  public void setCategory(int field, String name) {
    $(By.id("category"+field)).setValue(name);
  }
}
