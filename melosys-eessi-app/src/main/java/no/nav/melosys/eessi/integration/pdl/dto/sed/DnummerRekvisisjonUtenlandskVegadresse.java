package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonUtenlandskVegadresse extends DnummerRekvisisjonAdresse.Vegadresse {

    private String adressenavnNummer;
    private String bygningEtasjeLeilighet;
    private String postkode;
    private String bySted;
    private String regionDistriktOmraade;
    private String landkode;
}
