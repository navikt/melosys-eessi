package no.nav.melosys.eessi.integration.tps.aktoer;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
public class AktoerConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    //Ikke brukt _enda_ da aktørid hentes direkte fra gsak-id. Trengs ved inngående SED'er.
    public AktoerConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAktoerId(String ident) {
        if (!StringUtils.isEmpty(ident)) {
            String url = UriComponentsBuilder.fromPath("/identer")
                    .queryParam("identgruppe", "AktoerId")
                    .toUriString();
            log.info("Finding aktoerId for ident: {}", ident);
            JsonNode rootNode = restTemplate.exchange(url, HttpMethod.GET, headers(ident), JsonNode.class)
                    .getBody();

            if (rootNode != null) {
                JsonNode identNode = rootNode.path(ident).path("identer").path(0);
                if (identNode.isMissingNode()) {
                    log.warn("Could not find aktoerid in response, attempt to extract functional error: {}",
                            rootNode.get(ident));
                    return null;
                } else {
                    return identNode.get("ident").textValue();
                }
            }
            return null;
        }
        log.info("Ident is null, no need to find aktoerId");
        return null;
    }

    private HttpEntity<?> headers(String ident) {
        return new HttpEntity<>(new HttpHeaders() {{
            set("Nav-Personidenter", ident);
            set(ACCEPT, APPLICATION_JSON_VALUE);
            set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
            set("Nav-Call-Id","srvmelosys");
            set("Nav-Consumer-Id","srvmelosys");
        }});
    }
}
