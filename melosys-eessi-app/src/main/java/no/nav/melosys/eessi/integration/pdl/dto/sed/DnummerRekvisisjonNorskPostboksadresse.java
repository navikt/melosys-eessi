package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;
import no.nav.melosys.eessi.integration.pdl.dto.sed.adresse.Postboksadresse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonNorskPostboksadresse extends Postboksadresse {

    private String postboks;
    private String postnummer;
}
