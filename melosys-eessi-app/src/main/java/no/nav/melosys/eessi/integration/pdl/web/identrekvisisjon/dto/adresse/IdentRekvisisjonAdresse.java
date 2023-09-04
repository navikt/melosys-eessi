package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class IdentRekvisisjonAdresse {

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
