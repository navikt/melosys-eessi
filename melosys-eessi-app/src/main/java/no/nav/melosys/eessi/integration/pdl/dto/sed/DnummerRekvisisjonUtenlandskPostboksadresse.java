package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonUtenlandskPostboksadresse extends DnummerRekvisisjonAdresse.Postboksadresse {

    private String postboksNummerNavn;
    private String regionDistriktOmraade;
    private String postkode;
    private String bySted;
    private String landkode;
}
