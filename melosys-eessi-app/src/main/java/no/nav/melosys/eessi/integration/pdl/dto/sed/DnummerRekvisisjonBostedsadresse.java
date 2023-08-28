package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonBostedsadresse {

    private String gyldigFraOgMed;
    private DnummerRekvisisjonUtenlandskVegadresse utenlandskVegadresse;
}
