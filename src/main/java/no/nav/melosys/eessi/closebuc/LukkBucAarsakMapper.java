package no.nav.melosys.eessi.closebuc;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;

final class LukkBucAarsakMapper {

    private LukkBucAarsakMapper() {}

    private static final String LOVVALG_BEKREFTET = "gjeldende_lovgivning_det_ble_oppnådd_enighet_om_anmodningen_om_unntak";
    private static final String INGEN_SVAR_2_MND = "gjeldende_lovgivning_fastsettelsen_ble_endelig_ingen_reaksjon_innen_2_måneder";
    private static final String TRETTI_DAGER_SIDEN_A008 = "lovvalg_30_dager_siden_melding_om_relevant_informasjon";
    private static final String TRETTI_DAGER_SIDEN_MELDING_OM_UTSTASJONERING = "lovvalg_30_dager_siden_melding_om_utstasjonering";
    private static final String TRETTI_DAGER_SIDEN_SVAR_ANMODNING_MER_INFO = "gjeldende_lovgivning_30_dager_siden_svar_på_anmodning_om_mer_informasjon";


    static String hentAarsakForLukking(BUC buc) {
        BucType bucType = BucType.valueOf(buc.getBucType());
        switch (bucType) {
            case LA_BUC_02:
                if (inneholderSed(buc, SedType.A012)) {
                    return LOVVALG_BEKREFTET;
                }
                return INGEN_SVAR_2_MND;
            case LA_BUC_03:
                return TRETTI_DAGER_SIDEN_A008;
            case LA_BUC_04:
            case LA_BUC_05:
                return TRETTI_DAGER_SIDEN_MELDING_OM_UTSTASJONERING;
            case LA_BUC_06:
                return TRETTI_DAGER_SIDEN_SVAR_ANMODNING_MER_INFO;
            default:
                throw new IllegalArgumentException("Buctype" + bucType + " støttes ikke for lukking");
        }
    }

    private static boolean inneholderSed(BUC buc, SedType sedType) {
        return buc.getDocuments().stream().anyMatch(d -> sedType.name().equals(d.getType()));
    }
}
