package no.nav.melosys.eessi.integration;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@UtilityClass
public class RestUtils {

    public static String hentFeilmeldingForEux(RestClientException e) {
        if (e instanceof RestClientResponseException clientErrorException) {
            var feilmelding = clientErrorException.getResponseBodyAsString();
            if (!StringUtils.hasText(feilmelding)) return getMessageOrExceptionType(e);
            return feilmelding;
        }
        return getMessageOrExceptionType(e);
    }

    public static String hentFeilmeldingForPDLWeb(RestClientException e) {
        if (e instanceof RestClientResponseException clientErrorException) {
            var feilmelding = clientErrorException.getResponseBodyAsString();
            if (!StringUtils.hasText(feilmelding)) return getMessageOrExceptionType(e);
            return feilmelding;
        }
        return getMessageOrExceptionType(e);
    }

    static String getMessageOrExceptionType(Exception e) {
        if (e.getMessage() != null) return e.getMessage();
        return e.getClass().getSimpleName();
    }

    public static String hentFeilmeldingForOppgave(String feilmelding) {
        return feilmelding;
    }
}
