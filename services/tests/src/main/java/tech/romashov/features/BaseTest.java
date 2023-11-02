package tech.romashov.features;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import tech.romashov.core.AttachVideoListener;

@ContextConfiguration("classpath:spring.xml")
public class BaseTest extends AbstractTestNGSpringContextTests {
    @BeforeSuite
    public void selenideSetup() {
        Configuration.browser = "tech.romashov.core.SelenoidWebDriverProvider";
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
        SelenideLogger.addListener("AttachVideoListener", new AttachVideoListener());
    }
}
