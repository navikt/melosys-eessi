package no.nav.melosys.eessi.integration.eux.rina_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.EuxMelosysSedOppdateringDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    public void settSedJournalstatus(EuxMelosysSedOppdateringDto euxMelosysSedOppdateringDto) {
        log.info("Oppdaterer sed med ny status med Rina saksnummer {}", euxMelosysSedOppdateringDto.rinasakId());
        log.info("EuxMelosysSedOppdateringDto {}", euxMelosysSedOppdateringDto);
        exchange(SETT_SED_JOURNALSTATUS_PATH, HttpMethod.PUT,
            new HttpEntity<>(euxMelosysSedOppdateringDto, defaultHeaders()),
            new ParameterizedTypeReference<Void>() {});
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
