package no.nav.melosys.eessi.integration.saf;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.saf.dto.GraphQLResponse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class SafConsumer implements RestConsumer {

    private final RestTemplate restTemplate;

    private static final String QUERY = "{query: journalpost(journalpostId: \"%s\") {tilleggsopplysninger{nokkel verdi}}}";

    public SafConsumer(RestTemplate safRestTemplate) {
        this.restTemplate = safRestTemplate;
    }

    public Optional<String> hentRinasakForJournalpost(String journalpostID) {

        HttpEntity httpEntity = new HttpEntity(new GraphQLWrapper(String.format(QUERY, journalpostID), null), defaultHeaders());
        GraphQLResponse response = restTemplate.exchange("/graphql", HttpMethod.POST, httpEntity, GraphQLResponse.class).getBody();

        if (response == null) {
            log.info("Mottatt null-response fra SAF");
            return Optional.empty();
        } else if (response.harFeil()) {
            throw new IntegrationException("Feil ved integrasjon mot saf. Feilmeldinger: " + response.lagErrorString());
        }

        return response.getData().getQuery().hentRinaSakId();
    }

    @Data
    @AllArgsConstructor
    private static class GraphQLWrapper {
        private String query;
        private String variables;
    }
}
