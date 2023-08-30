package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
public class Matrikkeladresse {

    String bruksenhetstype;
    Integer undernummer;
    String adressetilleggsnavn;

    Matrikkelnummer matrikkelnummer;
    Poststed poststed;
}
