package no.nav.melosys.eessi.integration.eux.case_store;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class CaseStoreConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    private final ParameterizedTypeReference<List<CaseStoreDto>> getTypeReference = new ParameterizedTypeReference<List<CaseStoreDto>>(){};
    private final ParameterizedTypeReference<CaseStoreDto> postTypeReference = new ParameterizedTypeReference<CaseStoreDto>(){};

    public CaseStoreConsumer(@Qualifier("caseStoreResttemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CaseStoreDto> finnVedRinaSaksnummer(String rinaSaksnummer) {
        log.info("Søker på rinaSaksnummer {} i eux-case-store", rinaSaksnummer);
        return get("/cases?rinaId={rinaId}", rinaSaksnummer);
    }

    public List<CaseStoreDto> finnVedJournalpostID(String journalpostID) {
        log.info("Søker på journalpostId {} i eux-case-store", journalpostID);
        return get("/cases?caseFileId={caseFileId}", journalpostID);
    }

    public CaseStoreDto lagre(String gsakSaksnummer, String rinaSaksnummer) {
        return lagre(new CaseStoreDto(gsakSaksnummer, rinaSaksnummer));
    }

    public CaseStoreDto lagre(CaseStoreDto caseStoreDto) {
        log.info("Lagrer saksnummer {} og rinaSaksnummer {} i eux-case-store",
                caseStoreDto.getFagsaknummer(), caseStoreDto.getRinaSaksnummer());
        return post("/cases", caseStoreDto);
    }

    private List<CaseStoreDto> get(String uri, Object... variabler)  {
        return exchange(uri, HttpMethod.GET, new HttpEntity<>(defaultHeaders()), getTypeReference, variabler);
    }

    private CaseStoreDto post(String uri, Object dto)  {
        return exchange(uri, HttpMethod.POST, new HttpEntity<>(dto, defaultHeaders()), postTypeReference);
    }

    private <T> T exchange(String uri, HttpMethod method, HttpEntity<?> entity,
            ParameterizedTypeReference<T> responseType, Object... variabler)  {
        try {
            return restTemplate.exchange(uri, method, entity, responseType, variabler).getBody();
        } catch (RestClientException e) {
            throw new IntegrationException("Error in integration with eux-case-store: " + hentFeilmeldingForEux(e), e);
        }
    }
}
