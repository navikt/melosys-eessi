package no.nav.melosys.eessi.integration.dokkat;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokkat.api.tkat022.DokumenttypeIdTo;
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
public class DokumenttypeIdConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    public DokumenttypeIdConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DokumenttypeIdTo hentDokumenttypeId(final String eksternDokumenttypeId, final String eksternIdType)
            throws IntegrationException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, basicAuth());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            return restTemplate.exchange(String.format("/%s/%s", eksternDokumenttypeId, eksternIdType),
                    HttpMethod.GET, new HttpEntity<>(headers), DokumenttypeIdTo.class).getBody();

        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error("Feil ved integrasjon mot dokkat", e);
            throw new IntegrationException("Feil ved integrasjon mot dokkat", e);
        }
    }
}