package no.nav.melosys.eessi.controller.dto

import no.nav.melosys.eessi.models.sed.nav.Arbeidsland as RinaArbeidsland

data class Arbeidsland(
    var land: String? = null,
    var arbeidssted: List<Arbeidssted> = emptyList()
) {
    companion object {
        fun av(arbeidslandFraRina: RinaArbeidsland): Arbeidsland = Arbeidsland(
            land = arbeidslandFraRina.land,
            arbeidssted = arbeidslandFraRina.arbeidssted.map(Arbeidssted::av)
        )
    }
}
