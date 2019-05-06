package no.nav.melosys.eessi.models.buc;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BUC {

    private String id;
    private long startDate;
    private long lastUpdate;
    private String status;
    private Creator creator;
    private List<Document> documents;
    private List<Action> actions;
    @JsonProperty(value = "processDefinitionName")
    private String bucType;
}
