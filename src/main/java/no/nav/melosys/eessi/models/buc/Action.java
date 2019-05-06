package no.nav.melosys.eessi.models.buc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
    private String name;
    private String documentType;
}
