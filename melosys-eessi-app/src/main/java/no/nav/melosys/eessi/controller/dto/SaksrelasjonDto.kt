package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.FagsakRinasakKobling

data class SaksrelasjonDto(
    var gsakSaksnummer: Long? = null,
    var rinaSaksnummer: String? = null,
    var bucType: String? = null
) {
    companion object {
        @JvmStatic
        fun av(fagsakRinasakKobling: FagsakRinasakKobling): SaksrelasjonDto =
            SaksrelasjonDto(
                gsakSaksnummer = fagsakRinasakKobling.gsakSaksnummer,
                rinaSaksnummer = fagsakRinasakKobling.rinaSaksnummer,
                bucType = fagsakRinasakKobling.bucType.name
            )
    }
}
