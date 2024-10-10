package no.nav.melosys.eessi.service.sed

import no.nav.melosys.eessi.models.SedType
import java.util.*

object SedTypeTilTemaMapper {
    private const val TEMA_MED = "MED"
    private const val TEMA_UFM = "UFM"
    private val TEMA_UFM_SEDTYPER = EnumSet.of(SedType.A001, SedType.A003, SedType.A009, SedType.A010)

    @JvmStatic
    fun temaForSedType(sedType: String): String {
        val sedTypeEnum = SedType.valueOf(sedType)
        return if (TEMA_UFM_SEDTYPER.contains(sedTypeEnum)) TEMA_UFM else TEMA_MED
    }
}
