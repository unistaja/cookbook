import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PasswordChangeTest extends BaseSelenideTest {
  private final String newPassword = "tester";

  @Test
  public void testChangingPassword() {
    openPasswordChange();
    testEmptyFormResponse();
    testNullNewPasswordResponse();
    testEmptyNewPasswordResponse();
    testFalseOldPasswordResponse();
    testSuccessfulPasswordChange();
  }

  private void testEmptyFormResponse() {
    submitPasswordForm();
    $(Selectors.byText("Palun sisesta oma praegune parool.")).shouldBe(visible);
  }

  private void testNullNewPasswordResponse() {
    setOldPasswordValue("WrongPassword");
    submitPasswordForm();
    $(Selectors.byText("Palun sisesta uus parool.")).shouldBe(visible);
  }

  private void testEmptyNewPasswordResponse() {
    getNewPasswordField().clear();
    submitPasswordForm();
    $(Selectors.byText("Palun sisesta uus parool.")).shouldBe(visible);
  }

  private void testFalseOldPasswordResponse() {
    setNewPasswordValue(newPassword);
    submitPasswordForm();
    $(Selectors.byText("Parooli muutmine ebaõnnestus. Kontrolli vana parooli õigsust.")).shouldBe(visible);
  }

  private void testSuccessfulPasswordChange() {
    setOldPasswordValue(testPassword);
    submitPasswordForm();
    testPassword = newPassword;
    $(Selectors.byText("Parool edukalt muudetud!")).shouldBe(visible);
    $(Selectors.byText(testUsername)).click();
    $(Selectors.byText("Logi välja")).click();
    login();
    $(Selectors.byText(testUsername)).shouldBe(visible);
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