package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonKontaktadresse {

    private DnummerRekvisisjonNorskVegadresse norskVegadresse;
    private DnummerRekvisisjonNorskPostboksadresse norskPostboksadresse;
    private DnummerRekvisisjonUtenlandskVegadresse utenlandskVegadresse;
    private DnummerRekvisisjonUtenlandskPostboksadresse utenlandskPostboksadresse;
}
