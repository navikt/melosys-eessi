package no.nav.melosys.eessi.integration.journalpostapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class JournalpostapiConsumer {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Unleash unleash;

    public JournalpostapiConsumer(RestTemplate restTemplate, ObjectMapper objectMapper, Unleash unleash) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.unleash = unleash;
    }

    public OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean forsokEndeligJfr) {
        log.info("Oppretter journalpost av type {} for arkivsakid {}",
            request.getJournalpostType().name(), request.getSak() != null ? request.getSak().getArkivsaksnummer() : "ukjent");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("")
            .queryParam("forsoekFerdigstill", forsokEndeligJfr);

        try {
            return restTemplate.postForObject(uriBuilder.toUriString(), new HttpEntity<>(request, headers), OpprettJournalpostResponse.class);
        } catch (HttpClientErrorException.Conflict e) {
            if (unleash.isEnabled("melosys.eessi.opprettjournalpost")) {
                return getOpprettJournalpostResponse(e);
            } else {
                throw new SedAlleredeJournalførtException("SED allerede journalført", request.getEksternReferanseId());
            }
        }
    }

    private OpprettJournalpostResponse getOpprettJournalpostResponse(HttpClientErrorException.Conflict e) {
        try {
            return objectMapper.readValue(e.getResponseBodyAsString(), OpprettJournalpostResponse.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
