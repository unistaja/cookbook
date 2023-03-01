import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import ee.cookbook.CookbookApplication;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.FlywayTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit5.FlywayTestExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.openqa.selenium.devtools.v101.page.Page.close;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ExtendWith(SpringExtension.class)
@ExtendWith({FlywayTestExtension.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        FlywayTestExecutionListener.class })
@SpringBootTest(classes = CookbookApplication.class, webEnvironment=DEFINED_PORT)
@TestPropertySource(locations= "classpath:application.properties")
@FlywayTest(invokeCleanDB = true)
public abstract class BaseSelenideTest {

  String baseUrl;
  //account details
  final String testUsername = "Selenide";
  String testPassword = "test";
  //creating a user account
  @Value ("${spring.datasource.url}")
  public String flywayUrl;

  @Value ("${spring.datasource.username}")
  public String flywayUser;

  @Value ("${spring.datasource.password}")
  public String flywayPassword;

  @Value("${imageFolder}")
  public String imageFolder;

  @BeforeEach
  @FlywayTest(invokeCleanDB = true)
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
    $(By.cssSelector("button[type='submit']")).click();
  }

  public void refresh() {
    WebDriverRunner.getWebDriver().navigate().refresh();
  }
  @AfterEach
  public void closePage() {
    close();
  }

  @AfterEach
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

  public void openAddRecipePage() {
    $(Selectors.byText("Lisa retsept")).click();
  }

  public void openPasswordChange() {
    $(Selectors.byText(testUsername)).click();
    $(Selectors.byText("Muuda parooli")).click();
  }

  public void openSearchPage() throws InterruptedException {
    $(Selectors.byText("Otsing")).click();
    Thread.sleep(1000);
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
    $(By.id("category"+field)).click();
    $(By.id("category"+field)).setValue(name);
  }
}
