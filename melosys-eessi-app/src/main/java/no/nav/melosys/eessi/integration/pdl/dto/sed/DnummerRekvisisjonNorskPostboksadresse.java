package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonNorskPostboksadresse extends DnummerRekvisisjonAdresse.Postboksadresse {

    private String postboks;
    private String postnummer;
}
