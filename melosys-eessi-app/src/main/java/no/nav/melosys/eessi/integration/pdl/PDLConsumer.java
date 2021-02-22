package no.nav.melosys.eessi.integration.pdl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import no.nav.melosys.eessi.integration.common.graphql.request.GraphQLRequest;
import no.nav.melosys.eessi.integration.common.graphql.response.GraphQLResponse;
import no.nav.melosys.eessi.integration.pdl.dto.PDLHentPersonResponse;
import no.nav.melosys.eessi.integration.pdl.dto.PDLPerson;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class PDLConsumer {

    private final WebClient webClient;

    private static final String HENT_PERSON_QUERY;
    private static final String IDENT_KEY = "ident";

    public PDLConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public PDLPerson hentPerson(String ident) {
        var request = new GraphQLRequest(HENT_PERSON_QUERY, Map.of(IDENT_KEY, ident));

        var res = webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GraphQLResponse<PDLHentPersonResponse>>() {})
                .block();

        håndterFeil(res);
        return res.getData().getHentPerson();
    }

    private void håndterFeil(GraphQLResponse<?> res) {
        if (res == null) {
            throw new IntegrationException("Respons fra PDL er null!");
        } else if (res.harFeil()) {
            //TODO: korrekt exception for feil-kode
            throw new IntegrationException("Feil mot PDL. Feilmeldinger: " + res.lagErrorString());
        }
    }

    static {
        try {
            HENT_PERSON_QUERY = Files.readString(
                    Paths.get(Objects.requireNonNull(PDLConsumer.class.getClassLoader().getResource("pdl/hent_person_query")).toURI())
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
