package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DnummerRekvisisjonAdresse {

    String adressegradering;
    String adresseIdentifikatorFraMatrikkelen;
    String naerAdresseIdentifikatorFraMatrikkelen;
    String bruksenhetsnummer;
    String coAdressenavn;
    Long grunnkrets;

    UkjentBosted ukjentBosted;
    Vegadresse vegadresse;
    Matrikkeladresse matrikkeladresse;
    UtenlandskAdresse utenlandskAdresse;
    UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;
    Postboksadresse postboksadresse;
    VegadresseForPost vegadresseForPost;
    PostadresseIFrittFormat postadresseIFrittFormat;
}
