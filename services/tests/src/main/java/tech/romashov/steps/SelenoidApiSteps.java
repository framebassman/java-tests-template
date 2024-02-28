package tech.romashov.steps;

import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import tech.romashov.AssertWithTimeout;
import tech.romashov.requests.SelenoidRequests;

import java.time.Duration;

import static org.hamcrest.Matchers.equalTo;

@Component
public class SelenoidApiSteps {
    @Autowired
    private SelenoidRequests selenoidRequests;

    @Step("Wait for the Selenoid running")
    public void waitForRunningSelenoid() throws Throwable {
        AssertWithTimeout.assertThat(
                () -> {
                    try {
                        return selenoidRequests.ping().getStatusCode();
                    } catch (HttpClientErrorException | HttpServerErrorException exception) {
                        return exception.getStatusCode();
                    }
                },
                equalTo(HttpStatus.OK),
                Duration.ofSeconds(4)
        );
    }
}
