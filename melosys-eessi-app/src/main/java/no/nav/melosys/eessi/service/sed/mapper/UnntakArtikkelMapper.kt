package no.nav.melosys.eessi.service.sed.mapper

import no.nav.melosys.eessi.controller.dto.Bestemmelse

// Mappes til verdier som finnes i 'medlemskapsunntakartikkelkoder.properties' i eux-prosjektet.
object UnntakArtikkelMapper {
    private const val BESTEMMELSE_11_4 = "11_4"
    const val BESTEMMELSE_OTHER: String = "annet"

    @JvmStatic
    fun mapFromBestemmelse(bestemmelse: Bestemmelse?): String? =
        when (bestemmelse) {
            null -> null
            Bestemmelse.ART_11_4 -> BESTEMMELSE_11_4
            Bestemmelse.ART_11_1, Bestemmelse.ART_11_2 -> BESTEMMELSE_OTHER
            else -> bestemmelse.value
        }
}
