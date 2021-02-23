package no.nav.melosys.eessi.integration.common.graphql.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import org.springframework.util.CollectionUtils;

@Data
public class GraphQLResponse<T> {
    private List<GraphQLError> errors;
    private T data;

    public boolean harFeil() {
        return !CollectionUtils.isEmpty(errors) || data == null;
    }

    public String lagErrorString() {
        return errors.stream().map(GraphQLError::getMessage).collect(Collectors.joining("\n"));
    }
}
