package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.buc.Participant

data class BucinfoDto(
    var id: String? = null,
    var erÅpen: Boolean = false,
    var bucType: String? = null,
    var opprettetDato: Long? = null,
    var mottakerinstitusjoner: Set<String> = emptySet(),
    var seder: List<SedinfoDto> = emptyList()
) {
    companion object {
        @JvmStatic
        fun av(buc: BUC, statuser: List<String>?, rinaSedUrl: String): BucinfoDto = BucinfoDto(
            id = buc.id,
            erÅpen = buc.erÅpen(),
            bucType = buc.bucType,
            opprettetDato = buc.startDate!!.toInstant().toEpochMilli(),
            mottakerinstitusjoner = hentMottakerinstitusjonerFraBuc(buc),
            seder = buc.documents
                .filter { filtrerMedStatus(statuser, it) }
                .map { SedinfoDto.av(it, buc.id!!, rinaSedUrl) }
        )

        private fun filtrerMedStatus(statuser: List<String>?, document: Document): Boolean =
            statuser.isNullOrEmpty() || statuser.any { it == SedStatus.fraEngelskStatus(document.status)?.name }

        private fun hentMottakerinstitusjonerFraBuc(buc: BUC): Set<String> =
            buc.documents
                .flatMap { it.conversations }
                .flatMap { it.participants }
                .filter { it.role == Participant.ParticipantRole.MOTTAKER }
                .mapNotNull { it.organisation?.id }
                .toSet()
    }
}
