package tech.romashov.http;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import tech.romashov.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;

@Component
public class HttpClient {
    @Autowired
    private Logger logger;
    private RestTemplate template;
    public String lastExecutedRequestDescription;
    public String lastReceivedResponseDescription;

    public HttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .pingInterval(Duration.ofSeconds(10))
                .callTimeout(Duration.ofMinutes(1))
                .connectTimeout(Duration.ofMinutes(1))
                .readTimeout(Duration.ofMinutes(1))
                .writeTimeout(Duration.ofMinutes(1));
        OkHttpClient client = builder.build();
        template = new RestTemplate(new OkHttp3ClientHttpRequestFactory(client));
        DefaultUriBuilderFactory handler = new DefaultUriBuilderFactory();
        handler.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        template.setUriTemplateHandler(handler);
        template.getInterceptors().add(new CachingHttpRequestsInterceptor());
    }

    public HttpHeaders getHeaders(String uri) {
        return template.headForHeaders(uri);
    }

    class CachingHttpRequestsInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                            ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            lastExecutedRequestDescription =
                    httpRequest.getMethod() + " request to " + httpRequest.getURI() + " with body:\n" + new String(
                            bytes) + "\nand headers:\n" + httpRequest.getHeaders();
            ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
            lastReceivedResponseDescription =
                    "'" + response.getStatusText() + "' response with headers:\n" + response.getHeaders();
            return response;
        }
    }

    private <T> ResponseEntity<T> executeRestTemplateExchangeWithConversionErrorsHandling(
            Callable<ResponseEntity> execution) {
        try {
            return execution.call();
        } catch (RestClientException e) {
            logger.warn("\nIt was:\n" + lastExecutedRequestDescription + "\n\nAnd answered with:\n"
                            + lastReceivedResponseDescription);
            throw e;
        } catch (Exception e) {
            throw new RestClientException(
                    e.getMessage() + "\nIt was " + lastExecutedRequestDescription + "\nanswered with "
                            + lastReceivedResponseDescription, e);
        }
    }

    public <T> ResponseEntity<T> post(String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return executeRestTemplateExchangeWithConversionErrorsHandling(
                () -> template.exchange(url, HttpMethod.POST, requestEntity, responseType));
    }

    public <T> ResponseEntity<T> get(String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return executeRestTemplateExchangeWithConversionErrorsHandling(
                () -> template.exchange(url, HttpMethod.GET, requestEntity, responseType));
    }

    public <T> ResponseEntity<T> head(String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return executeRestTemplateExchangeWithConversionErrorsHandling(
                () -> template.exchange(url, HttpMethod.HEAD, requestEntity, responseType));
    }

    public <T> ResponseEntity<T> delete(String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return executeRestTemplateExchangeWithConversionErrorsHandling(
                () -> template.exchange(url, HttpMethod.DELETE, requestEntity, responseType));
    }
}
