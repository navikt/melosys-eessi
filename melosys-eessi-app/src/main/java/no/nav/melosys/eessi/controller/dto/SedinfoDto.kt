package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.buc.Document

data class SedinfoDto(
    var bucId: String? = null,
    var sedId: String? = null,
    var opprettetDato: Long? = null,
    var sistOppdatert: Long? = null,
    var sedType: String? = null,
    var status: String? = null,
    var rinaUrl: String? = null
) {
    companion object {
        fun av(document: Document, bucId: String, rinaSedUrl: String): SedinfoDto {
            return SedinfoDto(
                bucId = bucId,
                sedId = document.id,
                sedType = document.type,
                opprettetDato = document.creationDate!!.toInstant().toEpochMilli(),
                sistOppdatert = document.lastUpdate!!.toInstant().toEpochMilli(),
                status = tilNorskStatusEllerTomString(document.status),
                rinaUrl = rinaSedUrl
            )
        }

        private fun tilNorskStatusEllerTomString(status: String?): String {
            return SedStatus.fraEngelskStatus(status)?.norskStatus ?: ""
        }
    }
}
