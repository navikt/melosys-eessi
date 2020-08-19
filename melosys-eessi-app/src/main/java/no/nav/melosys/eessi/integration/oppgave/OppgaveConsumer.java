package no.nav.melosys.eessi.integration.oppgave;

import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.UUIDGenerator;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForOppgave;

public class OppgaveConsumer implements RestConsumer, UUIDGenerator {

    private final RestTemplate restTemplate;

    private static final String X_CORRELATION_ID = "X-Correlation-ID";

    public OppgaveConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpprettOppgaveResponseDto opprettOppgave(OppgaveDto oppgaveDto) {
        return exchange("/oppgaver", HttpMethod.POST, new HttpEntity<>(oppgaveDto, headers()), OpprettOppgaveResponseDto.class);
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
            Class<T> clazz) {
        try {
            return restTemplate.exchange(uri, method, entity, clazz).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Feil i integrasjon mot oppgave: " + hentFeilmeldingForOppgave(e), e);
        }
    }

    private HttpHeaders headers() {
        HttpHeaders headers = defaultHeaders();
        headers.add(X_CORRELATION_ID, generateUUID());
        return headers;
    }
}
