package no.nav.melosys.eessi.integration.eux.case_store;

import java.util.List;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static no.nav.melosys.eessi.integration.RestUtils.hentFeilmeldingForEux;

@Component
public class CaseStoreConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    private final ParameterizedTypeReference<List<CaseStoreDto>> typeReference = new ParameterizedTypeReference<List<CaseStoreDto>>(){};

    public CaseStoreConsumer(@Qualifier("caseStoreResttemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CaseStoreDto> finnVedRinaSaksnummer(String rinaSaksnummer) throws IntegrationException {
        return get("/cases?rinaId=" + rinaSaksnummer);
    }

    public List<CaseStoreDto> finnVedJournalpostID(String journalpostID) throws IntegrationException {
        return get("/cases?caseFileId=" + journalpostID);
    }

    private List<CaseStoreDto> get(String uri) throws IntegrationException {
        try {
            return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(defaultHeaders()), typeReference).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Error in integration with eux: " + hentFeilmeldingForEux(e), e);
        }
    }
}
