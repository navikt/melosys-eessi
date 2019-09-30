package no.nav.melosys.eessi.service.sed;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;

@UtilityClass
class SedUtils {

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
}

