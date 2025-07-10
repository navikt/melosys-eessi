package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.sed.nav.Pin
import no.nav.melosys.eessi.service.sed.LandkodeMapper

data class Ident(
    var ident: String? = null,
    var landkode: String? = null
) {
    companion object {
        fun av(pin: Pin): Ident = Ident(
            ident = pin.identifikator,
            landkode = LandkodeMapper.mapTilNavLandkode(pin.land)
        )
    }

    private fun erNorsk(): Boolean = landkode.equals("NO", ignoreCase = true)
    fun erUtenlandsk(): Boolean = !erNorsk()
}
