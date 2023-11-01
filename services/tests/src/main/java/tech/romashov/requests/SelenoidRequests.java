package tech.romashov.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;
import tech.romashov.Logger;
import tech.romashov.http.HttpClient;

@Component
public class SelenoidRequests {
    @Autowired
    private Logger logger;
    @Autowired
    private HttpClient httpClient;
    private String baseHost = "http://localhost:8080";
    private HttpEntity getDefaultHttpEntity() {
        return new HttpEntity<>("", new HttpHeaders());
    }

    public ResponseEntity<String> ping() {
        String uri = UriComponentsBuilder.fromUriString(baseHost)
                .path("ping")
                .build()
                .toUriString();
        logger.info(String.format("Create GET request to %s", uri));
        return httpClient.get(uri, getDefaultHttpEntity(), String.class);
    }
}
