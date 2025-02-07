package tech.romashov.requests;

import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import tech.romashov.core.Logger;
import tech.romashov.http.HttpClient;

@Component
public class SelenoidRequests {
    @Autowired
    private Logger logger;
    @Autowired
    private HttpClient httpClient;
    private String baseHost = "http://localhost:8080";
    private HttpEntity defaultHttpEntity = new HttpEntity<>("", new HttpHeaders());

    @Step("Let's ping Selenoid")
    public ResponseEntity<String> ping() {
        String uri = UriComponentsBuilder.fromUriString(baseHost)
                .path("ping")
                .build()
                .toUriString();
        logger.info(String.format("Create GET request to %s", uri));
        return httpClient.get(uri, defaultHttpEntity, String.class);
    }
}
