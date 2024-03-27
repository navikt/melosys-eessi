package no.nav.melosys.eessi.models;

import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DatoUtils {

    private static final int LOCAL_DATE_LENGTH = 10;

    public static LocalDate tilLocalDate(String dato) {
        return LocalDate.parse(formaterDatoString(dato));
    }

    private static String formaterDatoString(String dato) {
        return dato.substring(0, LOCAL_DATE_LENGTH);
    }

    public static LocalDate tilLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar != null ? LocalDate.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay()) : null;
    }
}
