package tech.romashov.features;

import com.codeborne.selenide.Selenide;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import tech.romashov.steps.SelenoidApiSteps;

public class SelenoidTests extends BaseTest {
    @Autowired
    private SelenoidApiSteps selenoidApiSteps;

    @Test
    public void itWorks() throws Throwable {
        selenoidApiSteps.waitForRunningSelenoid();
        Selenide.open("http://ya.ru");
    }
}