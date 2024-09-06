package no.nav.melosys.eessi.models

import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

object DatoUtils {
    private const val LOCAL_DATE_LENGTH = 10

    @JvmStatic
    fun tilLocalDate(dato: String): LocalDate = LocalDate.parse(
        dato.substring(0, LOCAL_DATE_LENGTH)
    )

    @JvmStatic
    fun tilLocalDate(xmlGregorianCalendar: XMLGregorianCalendar?): LocalDate? =
        xmlGregorianCalendar?.let {
            LocalDate.of(it.year, it.month, it.day)
        }
}
