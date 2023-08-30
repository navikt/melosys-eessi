package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class Vegadresse {

    String kommunenummer;
    String bydelsnummer;
    String brukesenhetstype;
    String adressenavn;
    String adressekode;
    String adressetilleggsnavn;

    Adressenummer adressenummer;
    Poststed poststed;
}
