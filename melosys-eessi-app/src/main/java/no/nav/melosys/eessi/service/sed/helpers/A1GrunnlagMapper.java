package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.models.exception.MappingException;

// Verdiene er hentet fra 'a1grunnlagskoder.properties' i eux-prosjektet.
class A1GrunnlagMapper {

    private A1GrunnlagMapper() {}

    private static final String BESTEMMELSE_12_R  = "12_r";
    private static final String BESTEMMELSE_16_R  = "16_R";
    private static final String BESTEMMELSE_OTHER = "annet";

    public static String mapFromBestemmelse(Bestemmelse bestemmelse) throws MappingException {
        switch (bestemmelse) {
            case ART_12_1:
            case ART_12_2:
                return BESTEMMELSE_12_R;
            case ART_16_1:
            case ART_16_2:
                return BESTEMMELSE_16_R;
            case ART_11_1:
            case ART_11_3_a:
            case ART_11_3_b:
            case ART_11_3_c:
            case ART_11_3_d:
            case ART_11_3_e:
            case ART_11_4:
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
                return BESTEMMELSE_OTHER;
            default:
                throw new MappingException("Kan ikke mappe til A1 Grunnlag for bestemmelse " + bestemmelse);
        }
    }
}
