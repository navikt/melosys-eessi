package no.nav.melosys.eessi.integration.journalpostapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class JournalpostapiConsumer {

    private final RestTemplate restTemplate;

    public JournalpostapiConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
            throw new SedAlleredeJournalførtException("SED allerede journalført", request.getEksternReferanseId());
        }
    }
}
