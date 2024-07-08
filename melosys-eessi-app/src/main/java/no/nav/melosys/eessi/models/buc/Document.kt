package no.nav.melosys.eessi.models.buc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.models.SedType
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Document(
    var id: String? = null,
    var creationDate: ZonedDateTime? = null, // TODO: gjør denne none-nullable
    var lastUpdate: ZonedDateTime? = null, // TODO: gjør denne none-nullable
    var creator: Creator? = null,
    var type: String? = null,
    var status: String? = null,
    var direction: String? = null,
    var conversations: List<Conversation> = ArrayList()
) {
    fun sedErSendt(): Boolean = conversations.isNotEmpty() && conversations[0].versionId != null && SedType.erLovvalgSed(type)

    fun erOpprettet(): Boolean {
        return !SedStatus.TOM.engelskStatus.equals(status, ignoreCase = true)
    }

    fun erX001(): Boolean = SedType.X001.name == type

    fun erIkkeX100(): Boolean = SedType.X100.name != type

    fun erLovvalgSED(): Boolean = SedType.erLovvalgSed(type)

    fun erAntallDagerSidenOppdatering(antallDagerSidenOppdatert: Long): Boolean =
        ZonedDateTime.now().minusDays(antallDagerSidenOppdatert).isAfter(lastUpdate)

    fun erInngående(): Boolean = "IN".equals(direction, ignoreCase = true)
}
