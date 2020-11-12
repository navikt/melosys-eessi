package no.nav.melosys.eessi.models.buc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        UTSENDER,
        @JsonProperty("Participant")
        DELTAKER,
        @JsonProperty("CounterParty")
        MOTPART
    }

    private ParticipantRole role;
    private Organisation organisation;
}

