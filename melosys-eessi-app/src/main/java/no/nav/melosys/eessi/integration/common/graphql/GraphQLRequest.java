package no.nav.melosys.eessi.integration.common.graphql;

import java.util.Map;

import lombok.Value;

@Value
public class GraphQLRequest {
    String query;
    Map<String, Object> variables;
}
