package no.nav.melosys.eessi.integration.dokarkivsed;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokarkivsed.api.v1.ArkiverUtgaaendeSed;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DokarkivSedConsumer {

    private final RestTemplate restTemplate;

    public DokarkivSedConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpprettUtgaaendeJournalpostResponse create(ArkiverUtgaaendeSed arkiverUtgaaendeSed)
            throws IntegrationException {
        try {
            log.info("Oppretter utgående journalpost for arkivsakid {}",
                    arkiverUtgaaendeSed.getForsendelsesinformasjon().getArkivSak().getArkivSakId());

            return restTemplate.postForObject("", arkiverUtgaaendeSed, OpprettUtgaaendeJournalpostResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IntegrationException("Feil ved oppretting av journalpost", e);
        }
    }
}
