package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse.Postboksadresse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonNorskPostboksadresse extends Postboksadresse {

    private String postboks;
    private String postnummer;
}
