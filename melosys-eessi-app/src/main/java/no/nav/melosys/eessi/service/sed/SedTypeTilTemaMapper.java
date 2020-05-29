package no.nav.melosys.eessi.service.sed;

import java.util.EnumSet;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.SedType;

@UtilityClass
public class SedTypeTilTemaMapper {

    private static final String TEMA_MED = "MED";
    private static final String TEMA_UFM = "UFM";

    private static final EnumSet<SedType> TEMA_UFM_SEDTYPER = EnumSet.of(
            SedType.A001, SedType.A009, SedType.A010
    );

    public static String temaForSedType(String sedType) {
        SedType sedTypeEnum = SedType.valueOf(sedType);
        return TEMA_UFM_SEDTYPER.contains(sedTypeEnum) ? TEMA_UFM : TEMA_MED;
    }

}
