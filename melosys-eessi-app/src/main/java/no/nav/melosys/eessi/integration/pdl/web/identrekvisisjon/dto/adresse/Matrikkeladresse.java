package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Matrikkeladresse {

    String bruksenhetstype;
    Integer undernummer;
    String adressetilleggsnavn;

    Matrikkelnummer matrikkelnummer;
    Poststed poststed;
}
