package no.nav.melosys.eessi.integration.dokkat;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.dokkat.dto.DokumentTypeInfoDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DokumenttypeInfoConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    public DokumenttypeInfoConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DokumentTypeInfoDto hentDokumenttypeInfo(final String dokumenttypeId) throws IntegrationException {
        try {

            return restTemplate
                .exchange("/{dokumenttypeId}", HttpMethod.GET, new HttpEntity<>(defaultHeaders()), DokumentTypeInfoDto.class, dokumenttypeId)
                .getBody();

        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Feil ved integrasjon mot dokkat", e);
            throw new IntegrationException("Feil ved integrasjon mot dokkat", e);
        }
    }
}

