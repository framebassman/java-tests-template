package tech.romashov.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.springframework.stereotype.Component;

@Component
public class YahooUiSteps {
    @Step("Verify capcha request at Yahoo login page")
    public void verifyYahooRobotCheck() {
        Selenide.open("https://login.yahoo.com/");
        Selenide.$("#login-username").sendKeys("framebassman@gmail.com");
        Selenide.$("#login-signin").click();
        Selenide.$("#recaptcha-iframe").shouldBe(Condition.visible);
    }
}
