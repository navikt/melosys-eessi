package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.JournalpostSedKobling

data class JournalpostSedKoblingDto(
    var journalpostID: String? = null,
    var sedID: String? = null,
    var rinaSaksnummer: String? = null,
    var bucType: String? = null,
    var sedType: String? = null
) {
    constructor(fra: JournalpostSedKobling) : this(
        journalpostID = fra.journalpostID,
        sedID = fra.sedId,
        rinaSaksnummer = fra.rinaSaksnummer,
        bucType = fra.bucType,
        sedType = fra.sedType
    )
}
