package no.nav.melosys.eessi.integration.gsak;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SakConsumer implements RestConsumer {

    private final static String MELOSYS_APPLIKASJON = "FS38";
    private final static String TEMA_MEDLEM = "MED";

    private final RestTemplate restTemplate;

    public SakConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Sak getSak(Long sakId) throws IntegrationException {

        HttpHeaders headers = headers();
        log.info("getSak: correlationId: {}, sakId: {}", headers.get("X-Correlation-ID"), sakId);

        return exchange("/" + Long.toString(sakId), HttpMethod.GET, new HttpEntity<>(headers), Sak.class);
    }

    public Sak createSak(String aktoerId) throws IntegrationException {

        SakDto sakDto = new SakDto();
        sakDto.setAktoerId(aktoerId);
        sakDto.setApplikasjon(MELOSYS_APPLIKASJON);
        sakDto.setTema(TEMA_MEDLEM);

        HttpHeaders headers = headers();
        log.info("createSak: correlationId: {}", headers.get("X-Correlation-ID"));

        return exchange("/", HttpMethod.POST, new HttpEntity<>(sakDto, headers), Sak.class);
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
            Class<T> clazz) throws IntegrationException {
        try {
            return restTemplate.exchange(uri, method, entity, clazz).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Feil i integrasjon mot gsak", e);
        }
    }

    private HttpHeaders headers() {
        String correlationId = generateUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("X-Correlation-ID", correlationId);

        return headers;
    }
}