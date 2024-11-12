package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.sed.nav.Arbeidssted as RinaArbeidssted
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper

data class Arbeidssted(
    var navn: String? = null,
    var adresse: Adresse, // kaster NullPointerException i Java kode om null
    var fysisk: Boolean = false,
    var hjemmebase: String? = null
) {
    companion object {
        fun av(arbeidsstedFraRina: RinaArbeidssted): Arbeidssted = Arbeidssted(
            navn = arbeidsstedFraRina.navn,
            adresse = Adresse.av(arbeidsstedFraRina.adresse),
            fysisk = mapFysisk(arbeidsstedFraRina.erikkefastadresse),
            hjemmebase = LandkodeMapper.mapTilNavLandkode(arbeidsstedFraRina.hjemmebase)
        )

        private fun mapFysisk(erIkkefastadresse: String?): Boolean =
            erIkkefastadresse.equals("nei", ignoreCase = true)
    }
}
