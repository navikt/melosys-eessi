// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.models;

import java.time.LocalDate;
import javax.xml.datatype.XMLGregorianCalendar;

public final class DatoUtils {
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

    @java.lang.SuppressWarnings("all")
    private DatoUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
