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
    private static final String EUX_STATUS_KEY = "status";
    private static final String OPPGAVE_FEIL_KEY = "feilmelding";

    public static String hentFeilmeldingForEux(RestClientException e) {
        if (e instanceof RestClientResponseException clientErrorException) {
            var feilmelding = clientErrorException.getResponseBodyAsString();
            if (!StringUtils.hasText(feilmelding)) return e.getMessage();
            return hentFeilmelding(feilmelding, EUX_FEIL_KEY);
        }
        return e.getMessage();
    }

    public static String hentFeilmeldingForOppgave(String feilmelding) {
        String message = hentFeilmelding(feilmelding, OPPGAVE_FEIL_KEY);
        if (message == null) {
            message = hentFeilmelding(feilmelding, EUX_STATUS_KEY);
        }
        return message;
    }

    private static String hentFeilmelding(String feilmelding, String nøkkel) {
        try {
            JsonNode json = objectMapper.readTree(feilmelding).path(nøkkel);
            return json.isMissingNode() ? feilmelding : json.toString();
        } catch (IOException ex) {
            log.warn("Kunne ikke lese feilmelding fra response", ex);
            return feilmelding;
        }
    }

}
