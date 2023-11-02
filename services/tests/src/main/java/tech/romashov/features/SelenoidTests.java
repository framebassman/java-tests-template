package tech.romashov.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import tech.romashov.steps.SelenoidApiSteps;
import tech.romashov.steps.YahooUiSteps;

public class SelenoidTests extends BaseTest {
    @Autowired
    private SelenoidApiSteps selenoidApiSteps;
    @Autowired
    private YahooUiSteps yahooUiSteps;

    @Test
    public void itWorks() throws Throwable {
        selenoidApiSteps.waitForRunningSelenoid();
        yahooUiSteps.verifyYahooRobotCheck();
    }
}
