package no.nav.melosys.eessi.models;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum BucType {
    LA_BUC_01,
    LA_BUC_02,
    LA_BUC_03,
    LA_BUC_04,
    LA_BUC_05,
    LA_BUC_06,

    H_BUC_01,
    H_BUC_02a,
    H_BUC_02b,
    H_BUC_02c,
    H_BUC_03a,
    H_BUC_03b,
    H_BUC_04,
    H_BUC_05,
    H_BUC_06,
    H_BUC_07,
    H_BUC_08,
    H_BUC_09,
    H_BUC_10,

    S_BUC_24,

    UB_BUC_01;

    private static final String LOVVALG_PREFIX = "LA";

    public boolean erLovvalgBuc() {
        return this.name().startsWith(LOVVALG_PREFIX);
    }

    // Multilateral = kan være flere enn 2 deltakere
    public boolean erMultilateralLovvalgBuc() {
        return this != LA_BUC_04;
    }

    // Betyr at buc-en brukes til å meddele et lovvalg med andre myndigheter
    public boolean meddelerLovvalg() {
        return this == LA_BUC_01 || this == LA_BUC_02 || this == LA_BUC_04 || this == LA_BUC_05;
    }

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

    public SedType hentFørsteLovligeSed() {
        if (!FØRSTE_LOVLIGE_SED_FRA_BUC_MAP.containsKey(this)) {
            throw new IllegalArgumentException("Melosys-eessi støtter ikke buctype " + this);
        }

        return FØRSTE_LOVLIGE_SED_FRA_BUC_MAP.get(this);
    }
}
