package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;

// Mappes til verdier som finnes i 'medlemskapsunntakartikkelkoder.properties' i eux-prosjektet.
public final class UnntakArtikkelMapper {

    private UnntakArtikkelMapper() {}

    private static final String BESTEMMELSE_11_4= "11_4";
    public static final String BESTEMMELSE_OTHER = "annet";

    public static String mapFromBestemmelse(Bestemmelse bestemmelse) {
        if (bestemmelse == null) {
            return null;
        }

        switch (bestemmelse) {
            case ART_11_4:
                return BESTEMMELSE_11_4;
            case ART_11_1:
                return BESTEMMELSE_OTHER;
            default:
                return bestemmelse.getValue();
        }
    }
}
