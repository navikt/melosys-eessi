package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.melosys.eessi.integration.pdl.dto.sed.adresse.Vegadresse;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonNorskVegadresse extends Vegadresse {

    private String adressenavn;
    private String husnummer;
    private String husbokstav;
    private String postnummer;
    private String bruksenhetsnummer;
    private String tilleggsnavn;
    private String matrikkelId;
}


