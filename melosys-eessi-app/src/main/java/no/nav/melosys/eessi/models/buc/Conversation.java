package no.nav.melosys.eessi.models.buc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Conversation {

    private String id;
    private String versionId;
    private List<Participant> participants;
}
