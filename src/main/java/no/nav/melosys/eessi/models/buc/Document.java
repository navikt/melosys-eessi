package no.nav.melosys.eessi.models.buc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    private String id;
    private String subProcessId;
    private long creationDate;
    private long lastUpdate;
    private Creator creator;
    private String type;
    private String status;
}
