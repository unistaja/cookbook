import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ee.cookbook.CookbookApplication;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import java.sql.*;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        FlywayTestExecutionListener.class })
@SpringApplicationConfiguration(classes = CookbookApplication.class)
@WebAppConfiguration
@IntegrationTest
@TestPropertySource(locations= "classpath:application-test.properties")
@FlywayTest(invokeCleanDB = true)



public class SelenideTestsBase {
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
    //creating a user account
    @Before
    public void setUpTest() {
      try {
        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost:3306/cookbooktest";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myUrl, "root", "test");
        String query = "INSERT INTO `cookbooktest`.`user` (`id`, `username`, `passwordHash`, `active`) VALUES ('1', 'Selenide', '$2a$10$gjZVQu8KKAAsWA3OafjGFOxoD/3kjOr7a2zhJsZnHf.Gs29Tsdp.W', 1);";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.execute();
        conn.close();
      } catch (Exception e) {
        System.err.println("Got an exception! ");
        System.err.println(e.getMessage());
      }
        open("/login");
        login();
    }

    //account details
    public String testusername = "Selenide";
    public String testpassword = "test";

    //logging in
    public void login() {
        $(By.name("username")).setValue(testusername);
        $(By.name("password")).setValue(testpassword);
        $(By.name("submit")).click();
    }

    public SelenideElement selectByPlaceholder(String placeholderValue) {
        return $(Selectors.byAttribute("placeholder", placeholderValue));
    }

    //going to a page
    public void openRecipeList() {
        $(Selectors.byText("Retseptid")).click();
    }

    public void openAddRecipePage() {
        $(Selectors.byText("Lisa retsept")).click();
    }

    public void openUserPage() {
        $(By.className("logout")).click();
    }
}
