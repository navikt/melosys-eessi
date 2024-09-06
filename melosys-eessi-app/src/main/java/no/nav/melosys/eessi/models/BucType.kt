package no.nav.melosys.eessi.models

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

enum class BucType {
    LA_BUC_01, LA_BUC_02, LA_BUC_03, LA_BUC_04, LA_BUC_05, LA_BUC_06, H_BUC_01, H_BUC_02a, H_BUC_02b, H_BUC_02c, H_BUC_03a, H_BUC_03b, H_BUC_04, H_BUC_05, H_BUC_06, H_BUC_07, H_BUC_08, H_BUC_09, H_BUC_10, S_BUC_24, UB_BUC_01;

    fun erLovvalgBuc(): Boolean = name.startsWith(LOVVALG_PREFIX)

    fun erMultilateralLovvalgBuc(): Boolean = this != LA_BUC_04

    fun meddelerLovvalg(): Boolean = this in setOf(LA_BUC_01, LA_BUC_02, LA_BUC_04, LA_BUC_05)

    fun hentFørsteLovligeSed(): SedType =
        FØRSTE_LOVLIGE_SED_FRA_BUC_MAP[this] ?: throw IllegalStateException("Melosys-eessi støtter ikke buctype $this")

    companion object {
        private const val LOVVALG_PREFIX = "LA"

        private val FØRSTE_LOVLIGE_SED_FRA_BUC_MAP = mapOf(
            LA_BUC_01 to SedType.A001,
            LA_BUC_02 to SedType.A003,
            LA_BUC_03 to SedType.A008,
            LA_BUC_04 to SedType.A009,
            LA_BUC_05 to SedType.A010,
            LA_BUC_06 to SedType.A005,
            H_BUC_01 to SedType.H001,
            H_BUC_02a to SedType.H005,
            H_BUC_02b to SedType.H004,
            H_BUC_02c to SedType.H003,
            H_BUC_03a to SedType.H010,
            H_BUC_03b to SedType.H011,
            H_BUC_04 to SedType.H020,
            H_BUC_05 to SedType.H061,
            H_BUC_06 to SedType.H065,
            H_BUC_07 to SedType.H070,
            H_BUC_08 to SedType.H120,
            H_BUC_09 to SedType.H121,
            H_BUC_10 to SedType.H130
        )

        @JvmStatic
        fun erHBucsomSkalKonsumeres(bucType: String): Boolean {
            val type = runCatching { valueOf(bucType) }.getOrElse {
                log.debug("Input buctype eksisterer ikke: $bucType")
                return false
            }
            return type in setOf(H_BUC_01, H_BUC_02a, H_BUC_02b, H_BUC_02c, H_BUC_03a, H_BUC_03b)
        }
    }
}
