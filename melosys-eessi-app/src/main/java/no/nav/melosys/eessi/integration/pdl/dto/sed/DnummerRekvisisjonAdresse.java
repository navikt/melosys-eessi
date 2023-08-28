package no.nav.melosys.eessi.integration.pdl.dto.sed;


import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

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

    @Value
    @Builder
    public static class UkjentBosted {
        String bostedskommune;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Vegadresse {
        String kommunenummer;
        String bydelsnummer;
        String brukesenhetstype;
        String adressenavn;
        String adressekode;
        String adressetilleggsnavn;

        Adressenummer adressenummer;
        Poststed poststed;
    }

    @Value
    @Builder
    public static class Adressenummer {
        String husnummer;
        String husbokstav;
    }

    @Value
    @Builder
    public static class Poststed {
        String poststedsnavn;
        String postnummer;
    }

    @Value
    @Builder
    public static class Matrikkeladresse {
        String bruksenhetstype;
        Integer undernummer;
        String adressetilleggsnavn;

        Matrikkelnummer matrikkelnummer;
        Poststed poststed;
    }

    @Value
    @Builder
    public static class Matrikkelnummer {
        String kommunenummer;
        int gaardsnummer;
        int bruksnummer;
        Integer festenummer;
    }

    @Value
    @Builder
    public static class UtenlandskAdresse {
        String adressenavnNummer;
        String bygningEtasjeLeilighet;
        String bygning;
        String etasjenummer;
        String boenhet;
        String postboksNummerNavn;
        String postkode;
        String bySted;
        String regionDistriktOmraade;
        String distriktsnavn;
        String region;
        String landkode;
    }

    @Value
    @Builder
    public static class UtenlandskAdresseIFrittFormat {
        List<String> adresselinje;
        String postkode;
        String byEllerStedsnavn;
        String landkode;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Postboksadresse {
        String postbokseier;
        Poststed poststed;
        String postboks;
    }

    @Value
    @Builder
    public static class VegadresseForPost {
        String adressenavn;
        Adressenummer adressenummer;
        String adressekode;
        String adressetilleggsnavn;
        Poststed poststed;
    }

    @Value
    @Builder
    public static class PostadresseIFrittFormat {
        List<String> adresselinje;
        Poststed poststed;
    }
}
