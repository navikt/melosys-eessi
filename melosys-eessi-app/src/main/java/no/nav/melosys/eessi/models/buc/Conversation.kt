package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Conversation(
    var id: String? = null,
    var versionId: String? = null,
    var participants: List<Participant> = listOf()
)
