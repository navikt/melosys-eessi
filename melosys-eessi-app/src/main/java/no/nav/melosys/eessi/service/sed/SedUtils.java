package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.Fagomraade;
import no.nav.melosys.eessi.models.SedType;

class SedUtils {

    private SedUtils() {
    }

    //Henter første lovlige SED på en ny BUC
    static SedType hentFoersteLovligeSedPaaBuc(BucType bucType) {
        SedType sedType = null;
        switch (bucType) {
            case LA_BUC_01:
                sedType = SedType.A001;
                break;
            case LA_BUC_02:
                sedType = SedType.A003;
                break;
            case LA_BUC_03:
                sedType = SedType.A008;
                break;
            case LA_BUC_04:
                sedType = SedType.A009;
                break;
            case LA_BUC_05:
                sedType = SedType.A010;
                break;
            case LA_BUC_06:
                sedType = SedType.A005;
                break;
            case H_BUC_02a:
                sedType = SedType.H005;
                break;
            default:
                throw new IllegalArgumentException("Melosys-eessi støtter ikke buctype " + bucType);
        }
        return sedType;
    }

    static Fagomraade hentFagomraadeForBuc(BucType bucType) {
        switch (bucType) {
            case LA_BUC_01:
            case LA_BUC_02:
            case LA_BUC_03:
            case LA_BUC_04:
            case LA_BUC_05:
            case LA_BUC_06:
                return Fagomraade.LOVVALG;
            case H_BUC_01:
            case H_BUC_02a:
            case H_BUC_02b:
            case H_BUC_02c:
            case H_BUC_03a:
            case H_BUC_03b:
            case H_BUC_05:
            case H_BUC_06:
            case H_BUC_07:
                return Fagomraade.HORISONTAL;
        }

        throw new IllegalArgumentException("Melosys-eessi støtter ikke buctype " + bucType);
    }

    static SedType getSedTypeFromBestemmelse(Bestemmelse bestemmelse) {

        switch (bestemmelse) {

            case ART_11_1:
            case ART_11_3_a:
            case ART_11_3_b:
            case ART_11_3_c:
            case ART_11_3_d:
            case ART_11_3_e:
            case ART_11_4_2:
                break;
            case ART_12_1:
            case ART_12_2:
                return SedType.A009;
            case ART_13_1_a:
            case ART_13_1_b_1:
            case ART_13_1_b_2:
            case ART_13_1_b_3:
            case ART_13_1_b_4:
            case ART_13_2_a:
            case ART_13_2_b:
            case ART_13_3:
            case ART_13_4:
            case ART_14_11:
                return SedType.A003;
            case ART_16_1:
            case ART_16_2:
                return SedType.A001;
        }

        throw new IllegalArgumentException("Lovvalgsbestemmelse " + bestemmelse.name() + " er ikke støttet enda!");
    }

    static BucType getBucTypeFromBestemmelse(Bestemmelse bestemmelse) {

        switch (bestemmelse) {

            case ART_11_1:
            case ART_11_3_a:
            case ART_11_3_b:
            case ART_11_3_c:
            case ART_11_3_d:
            case ART_11_3_e:
            case ART_11_4_2:
                break;
            case ART_12_1:
            case ART_12_2:
                return BucType.LA_BUC_04;
            case ART_13_1_a:
            case ART_13_1_b_1:
            case ART_13_1_b_2:
            case ART_13_1_b_3:
            case ART_13_1_b_4:
            case ART_13_2_a:
            case ART_13_2_b:
            case ART_13_3:
            case ART_13_4:
            case ART_14_11:
                return BucType.LA_BUC_02;
            case ART_16_1:
            case ART_16_2:
                return BucType.LA_BUC_01;

        }
        throw new IllegalArgumentException("Bestemmelse " + bestemmelse.name() + " er ikke støttet enda!");
    }
}

