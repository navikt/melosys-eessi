package no.nav.melosys.eessi.service.sts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class RestStsService implements RestConsumer {

    private static final Long EXPIRE_TIME_TO_REFRESH = 60L;

    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String EXPIRES_IN_KEY = "expires_in";

    private volatile LocalDateTime expiryTime = LocalDateTime.now();

    private String token;

    private final RestTemplate restTemplate;

    @Autowired
    public RestStsService(@Qualifier("restStsRestTemplate") RestTemplate restTemplate,
                          BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor) {
        this.restTemplate = restTemplate;
        restTemplate.getInterceptors().add(basicAuthClientRequestInterceptor);
    }

    public synchronized String collectToken() throws IntegrationException {
        if (shouldCollectNewToken()) {
            token = generateToken();
        }

        return token;
    }

    private String generateToken() throws IntegrationException {
        log.info("Henter oidc-token fra security-token-service");
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate
                    .exchange(createUriString(), HttpMethod.GET, createHttpEntity(),
                            new ParameterizedTypeReference<Map<String, Object>>() {
                            });

            Map<String, Object> responseBody = response.getBody();
            setExpiryTime(Long.valueOf(responseBody.get(EXPIRES_IN_KEY).toString()));

            return (String) responseBody.get(ACCESS_TOKEN_KEY);

        } catch (HttpStatusCodeException ex) {
            throw new IntegrationException("Error when connecting to reststs", ex);
        } catch (Exception ex) {
            throw new IntegrationException("Error when connecting to reststs", ex);
        }
    }

    private boolean shouldCollectNewToken() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    private void setExpiryTime(long expiryTime) {
        this.expiryTime = LocalDateTime.now()
                .plus(Duration.ofSeconds(expiryTime - EXPIRE_TIME_TO_REFRESH));
    }

    private String createUriString() {
        return UriComponentsBuilder.fromPath("/")
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid").toUriString();
    }

    private HttpEntity<?> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity(headers);
    }
}