package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Participant(
    var role: ParticipantRole? = null,
    var organisation: Organisation? = null // TODO: gjør denne none-nullable
) {
    enum class ParticipantRole {
        @JsonProperty("Receiver")
        MOTTAKER,

        @JsonProperty("Sender")
        UTSENDER,

        @JsonProperty("Participant")
        DELTAKER,

        @JsonProperty("CounterParty")
        MOTPART,

        @JsonProperty("CaseOwner")
        SAKSEIER
    }

    fun erMotpart() = role == ParticipantRole.MOTPART
}
