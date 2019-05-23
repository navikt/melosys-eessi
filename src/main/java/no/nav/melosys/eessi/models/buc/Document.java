package no.nav.melosys.eessi.models.buc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    private String id;
    @JsonProperty("subProcessId")
    private String bucId;
    private long creationDate;
    private long lastUpdate;
    private Creator creator;
    private String type;
    private String status;
}
