package no.nav.melosys.eessi.service.sed.helpers

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.models.exception.MappingException

// Verdiene er hentet fra 'a1grunnlagskoder.properties' i eux-prosjektet.
object A1GrunnlagMapper {
    private const val BESTEMMELSE_12_R = "12_r"
    private const val BESTEMMELSE_16_R = "16_R"
    private const val BESTEMMELSE_OTHER = "annet"

    @JvmStatic
    fun mapFromBestemmelse(bestemmelse: Bestemmelse): String {
        return when (bestemmelse) {
            Bestemmelse.ART_12_1, Bestemmelse.ART_12_2 -> BESTEMMELSE_12_R

            Bestemmelse.ART_16_1, Bestemmelse.ART_16_2 -> BESTEMMELSE_16_R

            Bestemmelse.ART_11_1, Bestemmelse.ART_11_2, Bestemmelse.ART_11_3_a, Bestemmelse.ART_11_3_b,
            Bestemmelse.ART_11_3_c, Bestemmelse.ART_11_3_d, Bestemmelse.ART_11_3_e, Bestemmelse.ART_11_4,
            Bestemmelse.ART_13_1_a, Bestemmelse.ART_13_1_b_1, Bestemmelse.ART_13_1_b_2, Bestemmelse.ART_13_1_b_3,
            Bestemmelse.ART_13_1_b_4, Bestemmelse.ART_13_2_a, Bestemmelse.ART_13_2_b, Bestemmelse.ART_13_3,
            Bestemmelse.ART_13_4, Bestemmelse.ART_14_11 -> BESTEMMELSE_OTHER

            else -> throw MappingException("Kan ikke mappe til A1 Grunnlag for bestemmelse $bestemmelse")
        }
    }
}
