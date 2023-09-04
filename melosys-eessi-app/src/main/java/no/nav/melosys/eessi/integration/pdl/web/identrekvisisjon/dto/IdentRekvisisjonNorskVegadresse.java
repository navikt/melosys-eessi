package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse.Vegadresse;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdentRekvisisjonNorskVegadresse extends Vegadresse {

    private String adressenavn;
    private String husnummer;
    private String husbokstav;
    private String postnummer;
    private String bruksenhetsnummer;
    private String tilleggsnavn;
    private String matrikkelId;
}


