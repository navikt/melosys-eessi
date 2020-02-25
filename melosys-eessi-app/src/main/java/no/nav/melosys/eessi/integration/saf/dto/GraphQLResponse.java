package no.nav.melosys.eessi.integration.saf.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import org.springframework.util.CollectionUtils;

@Data
public class GraphQLResponse {
    private List<GraphQLError> errors;
    private GraphQLQueryData data;

    public boolean harFeil() {
        return !CollectionUtils.isEmpty(errors) || data == null;
    }

    public String lagErrorString() {
        return errors.stream().map(GraphQLError::getMessage).collect(Collectors.joining("\n"));
    }
}
