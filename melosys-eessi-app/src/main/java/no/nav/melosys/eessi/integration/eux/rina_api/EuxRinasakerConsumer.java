package no.nav.melosys.eessi.integration.eux.rina_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.EuxVedlegg;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.SedJournalstatus;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForEux;

@Slf4j
public class EuxRinasakerConsumer implements RestConsumer {

    private final RestTemplate euxRestTemplate;
    private final ObjectMapper objectMapper;

    private static final String SETT_SED_JOURNALSTATUS_PATH = "/sed/journalstatuser";

    public EuxRinasakerConsumer(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.euxRestTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void settSedJournalstatus(String rinasakId, String sedId, Integer sedVersjon, SedJournalstatus sedJournalstatus) {
        log.info("Oppdaterer sed med ny status med Rina saksnummer {}", rinasakId);

        exchange(SETT_SED_JOURNALSTATUS_PATH, HttpMethod.PUT,
            new HttpEntity<>(defaultHeaders()),
            new ParameterizedTypeReference<Void>() {},
            rinasakId, sedId, sedVersjon, sedJournalstatus);
    }
    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
                           ParameterizedTypeReference<T> responseType, Object... variabler) {
        try {
            return euxRestTemplate.exchange(uri, method, entity, responseType, variabler).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("404 fra eux rinasaker: " + hentFeilmeldingForEux(e), e);
        } catch (RestClientException e) {
            throw new IntegrationException("Feil i integrasjon mot eux rinasaker: " + hentFeilmeldingForEux(e), e);
        }
    }
}
