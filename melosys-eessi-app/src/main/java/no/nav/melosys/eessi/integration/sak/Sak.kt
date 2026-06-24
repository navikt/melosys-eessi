package no.nav.melosys.eessi.integration.sak

import java.time.ZonedDateTime

data class Sak(
    var id: String? = null,
    var tema: String? = null,
    var applikasjon: String? = null,
    var aktoerId: String? = null,
    var orgnr: String? = null,
    var fagsakNr: String? = null,
    var opprettetAv: String? = null,
    var opprettetTidspunkt: ZonedDateTime? = null
)
