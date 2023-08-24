package no.nav.melosys.eessi.integration.pdl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.RestUtils;
import no.nav.melosys.eessi.integration.pdl.dto.sed.PDLSed;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;
import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForEux;
import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForPDLWeb;

@Slf4j
public class PDLRestConsumer implements RestConsumer {

    private final RestTemplate euxRestTemplate;

    private static final String PDL_SED_PATH = "/api/sed";


    public PDLRestConsumer(RestTemplate restTemplate) {
        this.euxRestTemplate = restTemplate;
    }

    public String hentPreutfylltLenkeForRekvirering(PDLSed pdlSed) {

        return exchange(PDL_SED_PATH, HttpMethod.POST,
            new HttpEntity<>(defaultHeaders()),
            new ParameterizedTypeReference<>() {},
            pdlSed);
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
                           ParameterizedTypeReference<T> responseType, Object... variabler) {
        try {
            return euxRestTemplate.exchange(uri, method, entity, responseType, variabler).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("404 fra PDL-web: " + hentFeilmeldingForPDLWeb(e), e);
        } catch (RestClientException e) {
            throw new IntegrationException("Feil i integrasjon mot PDL-web: " + hentFeilmeldingForPDLWeb(e), e);
        }
    }
}
