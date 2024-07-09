package no.nav.melosys.eessi.models

import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

class DatoUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private const val LOCAL_DATE_LENGTH = 10

        @JvmStatic
        fun tilLocalDate(dato: String): LocalDate {
            return LocalDate.parse(formaterDatoString(dato))
        }

        private fun formaterDatoString(dato: String): String {
            return dato.substring(0, LOCAL_DATE_LENGTH)
        }

        @JvmStatic
        fun tilLocalDate(xmlGregorianCalendar: XMLGregorianCalendar?): LocalDate? {
            return if (xmlGregorianCalendar != null) LocalDate.of(
                xmlGregorianCalendar.year,
                xmlGregorianCalendar.month,
                xmlGregorianCalendar.day
            ) else null
        }
    }
}
