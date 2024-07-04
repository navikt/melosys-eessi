package no.nav.melosys.eessi.integration.pdl;

import java.util.Map;

import no.nav.melosys.eessi.integration.common.graphql.request.GraphQLRequest;
import no.nav.melosys.eessi.integration.common.graphql.response.GraphQLResponse;
import no.nav.melosys.eessi.integration.pdl.dto.*;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import static no.nav.melosys.eessi.integration.pdl.PDLQuery.*;

public class PDLConsumer {

    private final WebClient webClient;

    private static final String IDENT_KEY = "ident";

    public PDLConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public PDLPerson hentPerson(String ident) {
        var request = new GraphQLRequest(HENT_PERSON_QUERY, Map.of(IDENT_KEY, ident));

        var res = webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<GraphQLResponse<PDLHentPersonResponse>>() {
            })
            .block();

        håndterFeil(res);
        return res.getData().getHentPerson();
    }

    public PDLSokPerson søkPerson(PDLSokRequestVars requestVars) {
        var request = new GraphQLRequest(SØK_PERSON_QUERY, requestVars);

        var res = webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<GraphQLResponse<PDLSokPersonResponse>>() {
            })
            .block();

        håndterFeil(res);
        return res.getData().getSokPerson();
    }

    public PDLIdentliste hentIdenter(String ident) {
        var request = new GraphQLRequest(HENT_IDENTER_QUERY, Map.of(IDENT_KEY, ident));

        var res = webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<GraphQLResponse<PDLHentIdenterResponse>>() {
            })
            .block();

        håndterFeil(res);
        return res.getData().getHentIdenter();
    }

    private void håndterFeil(GraphQLResponse<?> res) {
        if (res == null) {
            throw new IntegrationException("Respons fra PDL er null!");
        } else if (res.harFeil()) {
            //TODO: korrekt exception for feil-kode
            throw new IntegrationException("Feil mot PDL. Feilmeldinger: " + res.lagErrorString());
        }
    }
}
