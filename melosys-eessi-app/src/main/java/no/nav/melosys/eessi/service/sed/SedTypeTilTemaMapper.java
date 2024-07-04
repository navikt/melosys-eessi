// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.service.sed;

import java.util.EnumSet;
import no.nav.melosys.eessi.models.SedType;

public final class SedTypeTilTemaMapper {
    private static final String TEMA_MED = "MED";
    private static final String TEMA_UFM = "UFM";
    private static final EnumSet<SedType> TEMA_UFM_SEDTYPER = EnumSet.of(SedType.A001, SedType.A003, SedType.A009, SedType.A010);

    public static String temaForSedType(String sedType) {
        SedType sedTypeEnum = SedType.valueOf(sedType);
        return TEMA_UFM_SEDTYPER.contains(sedTypeEnum) ? TEMA_UFM : TEMA_MED;
    }

    @java.lang.SuppressWarnings("all")
    private SedTypeTilTemaMapper() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
