package no.nav.melosys.eessi.models.buc;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {

    @Getter
    public enum ParticipantRole {
        @JsonProperty("Receiver")
        MOTTAKER,
        @JsonProperty("Sender")
        UTSENDER
    }

    private ParticipantRole role;
    private Organisation organisation;
}

