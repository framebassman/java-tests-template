package tech.romashov.steps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import tech.romashov.matchers.AssertWithTimeout;
import tech.romashov.requests.SelenoidRequests;

import static org.hamcrest.Matchers.equalTo;

@Component
public class SelenoidApiSteps {
    @Autowired
    private SelenoidRequests selenoidRequests;

    public void waitForRunningSelenoid() throws Throwable {
        AssertWithTimeout.assertThat(
                () -> {
                    try {
                        return selenoidRequests.ping().getStatusCode();
                    } catch (HttpClientErrorException | HttpServerErrorException exception) {
                        return exception.getStatusCode();
                    }
                },
                equalTo(HttpStatus.OK)
        );
    }
}
