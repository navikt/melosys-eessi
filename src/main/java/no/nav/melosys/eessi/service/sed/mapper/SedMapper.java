package no.nav.melosys.eessi.service.sed.mapper;


import java.time.format.DateTimeFormatter;

/**
 * Felles mapper-interface for alle typer SED. Mapper NAV-objektet i NAV-SED, som brukes av eux for
 * å plukke ut nødvendig informasjon for en angitt SED.
 */
public interface SedMapper {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //Versjonen til SED'en. Generasjon og versjon (SED_G_VER.SED_VER = 4.1)
    String SED_G_VER = "4";
    String SED_VER = "1";
}
