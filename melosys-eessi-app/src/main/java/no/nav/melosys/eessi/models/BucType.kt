package no.nav.melosys.eessi.models

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

enum class BucType {
    LA_BUC_01, LA_BUC_02, LA_BUC_03, LA_BUC_04, LA_BUC_05, LA_BUC_06, H_BUC_01, H_BUC_02a, H_BUC_02b, H_BUC_02c, H_BUC_03a, H_BUC_03b, H_BUC_04, H_BUC_05, H_BUC_06, H_BUC_07, H_BUC_08, H_BUC_09, H_BUC_10, S_BUC_24, UB_BUC_01;

    fun erLovvalgBuc(): Boolean = name.startsWith(LOVVALG_PREFIX)

    // Multilateral = kan være flere enn 2 deltakere
    fun erMultilateralLovvalgBuc(): Boolean = this != LA_BUC_04

    // Betyr at buc-en brukes til å meddele et lovvalg med andre myndigheter
    fun meddelerLovvalg(): Boolean = this == LA_BUC_01 || (this == LA_BUC_02) || (this == LA_BUC_04) || (this == LA_BUC_05)

    fun hentFørsteLovligeSed(): SedType? {
        require(FØRSTE_LOVLIGE_SED_FRA_BUC_MAP.containsKey(this)) { "Melosys-eessi støtter ikke buctype $this" }
        return FØRSTE_LOVLIGE_SED_FRA_BUC_MAP[this]
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(BucType::class.java)
        private const val LOVVALG_PREFIX = "LA"

        private val FØRSTE_LOVLIGE_SED_FRA_BUC_MAP: Map<BucType, SedType> = Maps.immutableEnumMap(
            ImmutableMap.builder<BucType, SedType>().put(
                LA_BUC_01, SedType.A001
            ).put(LA_BUC_02, SedType.A003).put(LA_BUC_03, SedType.A008).put(LA_BUC_04, SedType.A009).put(LA_BUC_05, SedType.A010).put(
                LA_BUC_06, SedType.A005
            ).put(H_BUC_01, SedType.H001).put(H_BUC_02a, SedType.H005).put(H_BUC_02b, SedType.H004).put(H_BUC_02c, SedType.H003).put(
                H_BUC_03a, SedType.H010
            ).put(H_BUC_03b, SedType.H011).put(H_BUC_04, SedType.H020).put(H_BUC_05, SedType.H061).put(H_BUC_06, SedType.H065).put(
                H_BUC_07, SedType.H070
            ).put(H_BUC_08, SedType.H120).put(H_BUC_09, SedType.H121).put(H_BUC_10, SedType.H130).build()
        )

        @JvmStatic
        fun erHBucsomSkalKonsumeres(bucType: String): Boolean {
            val type: BucType
            try {
                type = valueOf(bucType)
            } catch (e: IllegalArgumentException) {
                log.debug("Input buctype eksisterer ikke: $bucType")
                return false
            }
            return Arrays.asList(H_BUC_01, H_BUC_02a, H_BUC_02b, H_BUC_02c, H_BUC_03a, H_BUC_03b).contains(type)
        }
    }
}
