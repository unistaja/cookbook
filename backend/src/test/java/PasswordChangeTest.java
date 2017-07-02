import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PasswordChangeTest extends BaseSelenideTest {
  private final String newPassword = "tester";

  @Test
  public void changingPasswordWorks() {
    openUserPage();
    emptyFormResponseTest();
    nullNewPasswordResponseTest();
    emptyNewPasswordResponseTest();
    falseOldPasswordResponseTest();
    successfulPasswordChangeTest();
  }

  private void emptyFormResponseTest() {
    submitPasswordForm();
    $(Selectors.byText("Palun sisesta oma praegune parool.")).shouldBe(visible);
  }

  private void nullNewPasswordResponseTest() {
    setOldPasswordValue("WrongPassword");
    submitPasswordForm();
    $(Selectors.byText("Palun sisesta uus parool.")).shouldBe(visible);
  }

  private void emptyNewPasswordResponseTest() {
    setNewPasswordValue("a");
    getNewPasswordField().sendKeys("\b");
    submitPasswordForm();
    $(Selectors.byText("Palun sisesta uus parool.")).shouldBe(visible);
  }

  private void falseOldPasswordResponseTest() {
    setNewPasswordValue(newPassword);
    submitPasswordForm();
    $(Selectors.byText("Parooli muutmine ebaõnnestus. Kontrolli vana parooli õigsust.")).shouldBe(visible);
  }

  private void successfulPasswordChangeTest() {
    setOldPasswordValue(testPassword);
    submitPasswordForm();
    testPassword = newPassword;
    $(Selectors.byText("Parool edukalt muudetud!")).shouldBe(visible);
    $(Selectors.byText("Logi välja")).click();
    login();
    $(By.className("logout")).shouldHave(exactTextCaseSensitive(testUsername));
  }

  private void setOldPasswordValue(String value) {
    selectByPlaceholder("Vana parool").setValue(value);
  }

  private void submitPasswordForm() {
    $(By.id("save")).click();
  }

  private SelenideElement getNewPasswordField() {
    return selectByPlaceholder("Uus parool");
  }

  private void setNewPasswordValue(String value) {
    getNewPasswordField().setValue(value);
  }

}
