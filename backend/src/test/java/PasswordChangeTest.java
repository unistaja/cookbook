/*
 * Created by Kaarel on 21.06.2017.
 */
import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import static com.codeborne.selenide.Condition.*;
import org.openqa.selenium.By;

public class PasswordChangeTest extends SelenideTestsBase {
    private String newPassword = "tester";

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
        selectByPlaceholder("Uus parool").setValue(value);
        return;
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
        setOldPasswordValue(testpassword);
        submitPasswordForm();
        testpassword = newPassword;
        $(Selectors.byText("Parool edukalt muudetud!")).shouldBe(visible);
        $(Selectors.byText("Logi välja")).click();
        login();
        $(By.className("logout")).shouldHave(exactTextCaseSensitive(testusername));
    }

    @Test
    public void changingPasswordWorks() {
        setUpTest();
        openUserPage();
        emptyFormResponseTest();
        nullNewPasswordResponseTest();
        emptyNewPasswordResponseTest();
        falseOldPasswordResponseTest();
        successfulPasswordChangeTest();
    }
}
