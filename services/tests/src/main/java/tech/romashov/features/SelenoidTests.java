package tech.romashov.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import tech.romashov.steps.SelenoidSteps;

public class SelenoidTests extends BaseTest {
    @Autowired
    private SelenoidSteps selenoidSteps;

    @Test
    public void itWorks() throws Throwable {
        selenoidSteps.waitForRunningSelenoid();
    }
}
