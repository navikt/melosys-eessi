package no.nav.melosys.eessi.integration.saf;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.common.graphql.request.GraphQLRequest;
import no.nav.melosys.eessi.integration.common.graphql.response.GraphQLResponse;
import no.nav.melosys.eessi.integration.saf.dto.SafResponse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class SafConsumer implements RestConsumer {

    private final WebClient webClient;

    private static final String QUERY = "{query: journalpost(journalpostId: \"%s\") {tilleggsopplysninger{nokkel verdi}}}";

    public SafConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<String> hentRinasakForJournalpost(String journalpostID) {

        Mono<GraphQLResponse<SafResponse>> responseMono = webClient.post()
            .uri("/graphql")
            .bodyValue(new GraphQLRequest(String.format(QUERY, journalpostID), null))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {
            });

        GraphQLResponse<SafResponse> response = responseMono.block();

        if (response == null) {
            log.info("Mottatt null-response fra SAF");
            return Optional.empty();
        } else if (response.harFeil()) {
            throw new IntegrationException("Feil ved integrasjon mot saf. Feilmeldinger: " + response.lagErrorString());
        }

        return response.getData().getQuery().hentRinaSakId();
    }
}
