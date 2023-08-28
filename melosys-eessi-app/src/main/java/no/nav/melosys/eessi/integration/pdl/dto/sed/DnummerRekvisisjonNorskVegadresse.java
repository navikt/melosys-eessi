package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonNorskVegadresse extends DnummerRekvisisjonAdresse.Vegadresse {

    private String adressenavn;
    private String husnummer;
    private String husbokstav;
    private String postnummer;
    private String bruksenhetsnummer;
    private String tilleggsnavn;
    private String matrikkelId;

}


