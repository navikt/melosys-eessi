package no.nav.melosys.eessi.integration.tps.aktoer;

import java.util.Optional;

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

    public String hentAktoerId(String ident) {

        String url = UriComponentsBuilder.fromPath("/identer")
                .queryParam("identgruppe", "AktoerId")
                .toUriString();
        log.info("Henter aktoerId for ident: {}", ident);
        JsonNode rootNode = restTemplate.exchange(url, HttpMethod.GET, headers(ident), JsonNode.class)
                .getBody();

        return hentGjeldende(rootNode, ident, "AktoerId")
                .orElseThrow(() -> new NotFoundException("Finner ikke aktørId for ident" + ident));

    }

    public String hentNorskIdent(String aktoerID) {
        String url = UriComponentsBuilder.fromPath("/identer")
                .queryParam("identgruppe", "NorskIdent")
                .toUriString();
        log.info("Henter ident for aktørID: {}", aktoerID);
        JsonNode rootNode = restTemplate.exchange(url, HttpMethod.GET, headers(aktoerID), JsonNode.class)
                .getBody();

        return hentGjeldende(rootNode, aktoerID, "NorskIdent")
                .orElseThrow(() -> new NotFoundException("Finner ikke ident for aktørID" + aktoerID));
    }

    private Optional<String> hentGjeldende(JsonNode res, String id, String identGruppe) {
        if (res == null) return Optional.empty();

        JsonNode identer = res.path(id).path("identer");
        for (JsonNode node : identer) {
            JsonNode erGjeldende = node.path("gjeldende");
            if (identGruppe.equalsIgnoreCase(node.get("identgruppe").textValue()) && !erGjeldende.isMissingNode() && erGjeldende.booleanValue()) {
                return Optional.of(node.get("ident").textValue());
            }
        }
        return Optional.empty();
    }

    private HttpEntity<?> headers(String ident) {
        HttpHeaders httpHeaders = defaultHeaders();
        httpHeaders.add("Nav-Personidenter", ident);
        httpHeaders.add("Nav-Call-Id","srvmelosys");
        httpHeaders.add("Nav-Consumer-Id","srvmelosys");
        return new HttpEntity<>(httpHeaders);
    }
}
