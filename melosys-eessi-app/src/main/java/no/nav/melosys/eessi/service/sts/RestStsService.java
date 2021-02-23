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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public String bearerToken() {
        return "Bearer " + collectToken();
    }

    public synchronized String collectToken() {
        if (shouldCollectNewToken()) {
            token = generateToken();
        }

        return token;
    }

    private String generateToken() {
        log.info("Henter oidc-token fra security-token-service");
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate
                    .exchange(createUriString(), HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                            new ParameterizedTypeReference<Map<String, Object>>() {
                            });

            Map<String, Object> responseBody = response.getBody();
            setExpiryTime(Long.parseLong(responseBody.get(EXPIRES_IN_KEY).toString()));

            return (String) responseBody.get(ACCESS_TOKEN_KEY);

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
        return UriComponentsBuilder.fromPath("/token")
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid").toUriString();
    }
}
