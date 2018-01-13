import com.codeborne.selenide.Selectors;
import org.junit.Test;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Selenide.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;

public class UnauthorizedAccessTest extends BaseSelenideTest {
  @Test
  public void testOfflineAccess() {
    $(Selectors.byText("Logi välja")).click();
    $(Selectors.byText("Logi välja")).shouldNotBe(visible);
    $(By.id("navbar")).shouldNotBe(visible);
    testPage("recipelist");
    testPage("addrecipe");
    testPage("user");
  }

  private void testPage(String page) {
    open("/#/"+page);
    assert url().equals(baseUrl + "login#/"+page);
    $(By.id("app")).shouldNotBe(visible);
  }
}
