package no.nav.melosys.eessi.integration.dokmotinngaaende;

import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseRequest;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseResponse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class DokmotInngaaendeConsumer {

    private final RestTemplate restTemplate;

    public DokmotInngaaendeConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MottaInngaaendeForsendelseResponse create(
            MottaInngaaendeForsendelseRequest mottaInngaaendeForsendelseRequest) throws IntegrationException {

        try {
            return restTemplate.postForObject("/mottaInngaaendeForsendelse",
                    mottaInngaaendeForsendelseRequest,
                    MottaInngaaendeForsendelseResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IntegrationException("Feil ved oppretting av journalpost", e);
        }
    }
}
