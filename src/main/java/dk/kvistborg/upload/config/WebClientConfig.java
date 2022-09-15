package dk.kvistborg.upload.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * This class is used to create Webclient object.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    private static final String VOTIRO_SERVICE = "votiro";

    @Value("${application.host}")
    private String host;

    /**
     * Error handling filter function that creates a WebclientResponseException when the http status code is other than 200 series.
     *
     * @param serviceName service name
     * @return Exchange filter function for error handling
     */
    private static ExchangeFilterFunction errorHandlingFilter(String serviceName) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            final HttpStatus httpStatus = HttpStatus.resolve(clientResponse.statusCode().value());
            if (httpStatus != null && httpStatus.is2xxSuccessful()) {
                log.info("{} : SUCCESS Response from :: {} ", httpStatus, serviceName);
                return Mono.just(clientResponse);
            }
            log.error("{} : ERROR Response from :: {}", httpStatus, serviceName);
            return clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                switch (httpStatus) {
                    case UNAUTHORIZED: return Mono.error(new ResponseStatusException(httpStatus));
                    case BAD_REQUEST: return  Mono.error(new ResponseStatusException(httpStatus));
                    case FORBIDDEN: return Mono.error(new ResponseStatusException(httpStatus));
                    case NOT_FOUND: return Mono.error(new ResponseStatusException(httpStatus));
                    case INTERNAL_SERVER_ERROR: return Mono.error(new ResponseStatusException(httpStatus));
                    default: return Mono.error(new WebClientResponseException(
                            clientResponse.statusCode().value(),
                            httpStatus.getReasonPhrase(),
                            clientResponse.headers().asHttpHeaders(),
                            errorBody.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                }
            });
        });
    }

    @Bean
    WebClient votiroWebClient(WebClient.Builder builder) {
        var client = builder.filter(errorHandlingFilter(VOTIRO_SERVICE));
        return buildWebClientNoCertificate(client, host);
    }

    private WebClient buildWebClientNoCertificate(WebClient.Builder builder, String host) {
            return builder
                    .baseUrl(host)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MULTIPART_FORM_DATA_VALUE)
                    .build();
    }
}
