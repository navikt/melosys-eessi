package no.nav.melosys.eessi.models.sed;

import java.time.format.DateTimeFormatter;

public final class Constants {

    private Constants() {}

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Versjonen til SED'en. Generasjon og versjon (SED_G_VER.SED_VER = 4.1)
    public static final String SED_G_VER = "4";
    public static final String SED_VER = "1";
}
