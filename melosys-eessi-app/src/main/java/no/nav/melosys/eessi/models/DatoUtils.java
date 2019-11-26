package no.nav.melosys.eessi.models;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class DatoUtils {

    private static int LOCAL_DATE_LENGTH = 10;

    private static String formaterDatoString(String dato) {
        return dato.substring(0, LOCAL_DATE_LENGTH);
    }

    public static LocalDate tilLocalDate(String dato) {
        return LocalDate.parse(formaterDatoString(dato));
    }
}
