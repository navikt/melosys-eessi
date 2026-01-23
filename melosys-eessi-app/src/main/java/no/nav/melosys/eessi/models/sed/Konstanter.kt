package no.nav.melosys.eessi.models.sed

import java.time.format.DateTimeFormatter

object Konstanter {

    @JvmField
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    //Versjonen til SED'en. Generasjon og versjon (SED_G_VER.SED_VER = 4.2)
    const val SED_VER_CDM_4_3: String = "3"
    const val SED_VER_CDM_4_4: String = "4"
    const val DEFAULT_SED_VER: String = "2"
    const val DEFAULT_SED_G_VER: String = "4"
}
