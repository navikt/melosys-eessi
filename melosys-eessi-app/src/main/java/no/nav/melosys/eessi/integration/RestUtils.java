package no.nav.melosys.eessi.integration;

import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@UtilityClass
public class RestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String EUX_FEIL_KEY = "messages";
    private static final String OPPGAVE_FEIL_KEY = "feilmelding";

    public static String hentFeilmeldingForEux(RestClientException e) {
        return hentFeilmelding(e, EUX_FEIL_KEY);
    }

    public static String hentFeilmeldingForOppgave(RestClientException e) {
        return hentFeilmelding(e, OPPGAVE_FEIL_KEY);
    }

    private static String hentFeilmelding(RestClientException e, String nøkkel) {
        if(e instanceof RestClientResponseException) {
            RestClientResponseException clientErrorException = (RestClientResponseException) e;
            String feilmelding = clientErrorException.getResponseBodyAsString();
            if (StringUtils.isEmpty(feilmelding)) return e.getMessage();
            try {
                JsonNode json = objectMapper.readTree(feilmelding).path(nøkkel);
                return json.isMissingNode() ? e.getMessage() : json.toString();
            } catch (IOException ex) {
                log.warn("Kunne ikke lese feilmelding fra response", ex);
                return clientErrorException.getResponseBodyAsString();
            }
        }

        return e.getMessage();
    }

}
