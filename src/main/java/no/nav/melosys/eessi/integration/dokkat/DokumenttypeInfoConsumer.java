package no.nav.melosys.eessi.integration.dokkat;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokkat.api.tkat020.v4.DokumentTypeInfoToV4;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DokumenttypeInfoConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    public DokumenttypeInfoConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DokumentTypeInfoToV4 hentDokumenttypeInfo(final String dokumenttypeId) throws IntegrationException {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, basicAuth());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            return restTemplate
                .exchange("/" + dokumenttypeId, HttpMethod.GET, new HttpEntity<>(headers), DokumentTypeInfoToV4.class)
                .getBody();

        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Feil ved integrasjon mot dokkat", e);
            throw new IntegrationException("Feil ved integrasjon mot dokkat", e);
        }
    }
}

