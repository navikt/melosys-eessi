package no.nav.melosys.eessi.models.sed;

import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Konstanter {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Versjonen til SED'en. Generasjon og versjon (SED_G_VER.SED_VER = 4.2)
    public static final String DEFAULT_SED_G_VER = "4";
    public static final String DEFAULT_SED_VER = "3";
}
