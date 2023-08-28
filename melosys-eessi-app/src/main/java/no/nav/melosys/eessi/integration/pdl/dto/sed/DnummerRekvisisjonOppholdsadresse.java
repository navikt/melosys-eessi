package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonOppholdsadresse {

    private String gyldigFraOgMed;
    private DnummerRekvisisjonNorskVegadresse norskVegadresse;
    private DnummerRekvisisjonUtenlandskVegadresse utenlandskVegadresse;
}
