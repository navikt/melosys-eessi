package no.nav.melosys.eessi.service.sed;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;

@UtilityClass
class SedUtils {
    private static final Map<BucType, SedType> FØRSTE_LOVLIGE_SED_FRA_BUC_MAP =
            Maps.immutableEnumMap(ImmutableMap.<BucType, SedType>builder()
                    .put(BucType.LA_BUC_01, SedType.A001)
                    .put(BucType.LA_BUC_02, SedType.A003)
                    .put(BucType.LA_BUC_03, SedType.A008)
                    .put(BucType.LA_BUC_04, SedType.A009)
                    .put(BucType.LA_BUC_05, SedType.A010)
                    .put(BucType.LA_BUC_06, SedType.A005)

                    .put(BucType.H_BUC_01, SedType.H001)
                    .put(BucType.H_BUC_02a, SedType.H005)
                    .put(BucType.H_BUC_02b, SedType.H004)
                    .put(BucType.H_BUC_02c, SedType.H003)
                    .put(BucType.H_BUC_03a, SedType.H010)
                    .put(BucType.H_BUC_03b, SedType.H011)
                    .put(BucType.H_BUC_04, SedType.H020)
                    .put(BucType.H_BUC_05, SedType.H061)
                    .put(BucType.H_BUC_06, SedType.H065)
                    .put(BucType.H_BUC_07, SedType.H070)
                    .put(BucType.H_BUC_08, SedType.H120)
                    .put(BucType.H_BUC_09, SedType.H121)
                    .put(BucType.H_BUC_10, SedType.H130)

                    .build());

    //Henter første lovlige SED på en ny BUC
    static SedType hentFørsteLovligeSedPåBuc(BucType bucType) {
        if (!FØRSTE_LOVLIGE_SED_FRA_BUC_MAP.containsKey(bucType)) {
            throw new IllegalArgumentException("Melosys-eessi støtter ikke buctype " + bucType);
        }

        return FØRSTE_LOVLIGE_SED_FRA_BUC_MAP.get(bucType);
    }
}

