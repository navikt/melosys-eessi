package no.nav.melosys.eessi.integration.tps.aktoer;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class AktoerConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    public AktoerConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String hentAktoerId(String ident) throws NotFoundException {

        String url = UriComponentsBuilder.fromPath("/identer")
                .queryParam("identgruppe", "AktoerId")
                .toUriString();
        log.info("Henter aktoerId for ident: {}", ident);
        JsonNode rootNode = restTemplate.exchange(url, HttpMethod.GET, headers(ident), JsonNode.class)
                .getBody();

        if (rootNode != null) {
            return hentAktoerIdFraResponse(rootNode, ident);
        } else {
            throw new NotFoundException("Finner ikke aktørId for ident" + ident);
        }
    }

    public String hentNorskIdent(String aktoerID) throws NotFoundException {
        String url = UriComponentsBuilder.fromPath("/identer")
                .queryParam("identgruppe", "NorskIdent")
                .toUriString();
        log.info("Henter ident for aktørID: {}", aktoerID);
        JsonNode rootNode = restTemplate.exchange(url, HttpMethod.GET, headers(aktoerID), JsonNode.class)
                .getBody();

        if (rootNode != null) {
            return hentNorskIdentFraResponse(rootNode, aktoerID);
        } else {
            throw new NotFoundException("Finner ikke ident for aktørID" + aktoerID);
        }
    }

    private String hentAktoerIdFraResponse(JsonNode rootNode, String ident) throws NotFoundException {
        JsonNode identNode = rootNode.path(ident).path("identer").path(0);

        if (identNode.isMissingNode()) {
            throw new NotFoundException("Finner ikke aktørId for ident " + ident);
        }

        return identNode.get("ident").textValue();
    }

    private String hentNorskIdentFraResponse(JsonNode rootNode, String aktoerID) throws NotFoundException {
        JsonNode identNode = rootNode.path(aktoerID).path("identer").path(0);

        if (identNode.isMissingNode()) {
            throw new NotFoundException("Finner ikke norsk ident for aktørId " + aktoerID);
        }

        return identNode.get("ident").textValue();
    }

    private HttpEntity<?> headers(String ident) {
        HttpHeaders httpHeaders = defaultHeaders();
        httpHeaders.add("Nav-Personidenter", ident);
        httpHeaders.add("Nav-Call-Id","srvmelosys");
        httpHeaders.add("Nav-Consumer-Id","srvmelosys");
        return new HttpEntity<>(httpHeaders);
    }
}
