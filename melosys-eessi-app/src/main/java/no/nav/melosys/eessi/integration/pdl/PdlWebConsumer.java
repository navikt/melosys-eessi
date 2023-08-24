package no.nav.melosys.eessi.integration.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.pdl.dto.sed.PDLSed;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForPDLWeb;

@Slf4j
public class PdlWebConsumer implements RestConsumer {

    private final WebClient webClient;

    private static final String PDL_SED_PATH = "/api/sed";


    public PdlWebConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public String hentPreutfylltLenkeForRekvirering(PDLSed pdlSed) {
        return webClient
            .post()
            .uri(PDL_SED_PATH)
            .bodyValue(pdlSed)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
