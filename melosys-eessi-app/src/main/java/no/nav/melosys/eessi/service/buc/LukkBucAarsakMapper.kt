package no.nav.melosys.eessi.service.buc

import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.BUC

object LukkBucAarsakMapper {

    private const val LOVVALG_BEKREFTET = "gjeldende_lovgivning_det_ble_oppnådd_enighet_om_anmodningen_om_unntak"
    private const val INGEN_SVAR_2_MND = "gjeldende_lovgivning_fastsettelsen_ble_endelig_ingen_reaksjon_innen_2_måneder"
    private const val TRETTI_DAGER_SIDEN_A008 = "lovvalg_30_dager_siden_melding_om_relevant_informasjon"
    private const val TRETTI_DAGER_SIDEN_MELDING_OM_UTSTASJONERING = "lovvalg_30_dager_siden_melding_om_utstasjonering"
    private const val TRETTI_DAGER_SIDEN_SVAR_ANMODNING_MER_INFO = "gjeldende_lovgivning_30_dager_siden_svar_på_anmodning_om_mer_informasjon"
    private const val ENIGHET_ANMODNING_UNNTAK = "gjeldende_lovgivning_det_ble_oppnådd_enighet_om_anmodningen_om_unntak"

    @JvmStatic
    fun hentAarsakForLukking(buc: BUC): String {
        val bucType = BucType.valueOf(buc.bucType!!)
        return when (bucType) {
            BucType.LA_BUC_01 -> ENIGHET_ANMODNING_UNNTAK
            BucType.LA_BUC_02 -> if (a012SendtFraBuc(buc)) LOVVALG_BEKREFTET else INGEN_SVAR_2_MND
            BucType.LA_BUC_03 -> TRETTI_DAGER_SIDEN_A008
            BucType.LA_BUC_04, BucType.LA_BUC_05 -> TRETTI_DAGER_SIDEN_MELDING_OM_UTSTASJONERING
            BucType.LA_BUC_06 -> TRETTI_DAGER_SIDEN_SVAR_ANMODNING_MER_INFO
            else -> throw IllegalArgumentException("Buctype $bucType støttes ikke for lukking")
        }
    }

    private fun a012SendtFraBuc(buc: BUC): Boolean = buc.documents.any {
        SedType.A012.name == it.type && "empty" != it.status
    }
}
