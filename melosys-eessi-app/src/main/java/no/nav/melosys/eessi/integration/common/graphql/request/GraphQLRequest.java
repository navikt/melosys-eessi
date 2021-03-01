package no.nav.melosys.eessi.integration.common.graphql.request;

import lombok.Value;

@Value
public class GraphQLRequest {
    String query;
    Object variables;
}
