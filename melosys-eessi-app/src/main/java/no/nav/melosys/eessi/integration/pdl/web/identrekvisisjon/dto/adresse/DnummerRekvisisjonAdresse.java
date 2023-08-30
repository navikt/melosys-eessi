package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;


@Getter
@AllArgsConstructor
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
