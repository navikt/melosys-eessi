package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VegadresseForPost {

    String adressenavn;
    Adressenummer adressenummer;
    String adressekode;
    String adressetilleggsnavn;
    Poststed poststed;
}
