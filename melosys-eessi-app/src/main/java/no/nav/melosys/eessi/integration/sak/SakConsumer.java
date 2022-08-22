package no.nav.melosys.eessi.integration.sak;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.config.MDCOperations;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SakConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    public SakConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Sak getSak(String arkivsakId) {

        HttpHeaders headers = headers();
        log.info("hentsak: correlationId: {}, sakId: {}", headers.get(MDCOperations.X_CORRELATION_ID), arkivsakId);

        return exchange("/{arkivsakId}", HttpMethod.GET, new HttpEntity<>(headers), Sak.class, arkivsakId);
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity, Class<T> clazz, Object... variabler) {
        try {
            return restTemplate.exchange(uri, method, entity, clazz, variabler).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Feil i integrasjon mot sak", e);
        }
    }

    private HttpHeaders headers() {
        HttpHeaders headers = defaultHeaders();
        return headers;
    }
}
