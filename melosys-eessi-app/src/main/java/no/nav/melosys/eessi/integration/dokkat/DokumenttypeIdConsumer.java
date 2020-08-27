package no.nav.melosys.eessi.integration.dokkat;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DokumenttypeIdConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    public DokumenttypeIdConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DokumenttypeIdDto hentDokumenttypeId(final String eksternDokumenttypeId, final String eksternIdType) {
        try {

            return restTemplate.exchange("/{eksternDokumenttypeId}/{eksternIdType}",
                    HttpMethod.GET, new HttpEntity<>(defaultHeaders()), DokumenttypeIdDto.class, eksternDokumenttypeId, eksternIdType).getBody();

        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Feil ved integrasjon mot dokkat", e);
            throw new IntegrationException("Feil ved integrasjon mot dokkat", e);
        }
    }
}
