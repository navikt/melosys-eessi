package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;
import no.nav.melosys.eessi.integration.pdl.dto.sed.adresse.DnummerRekvisisjonAdresse;
import no.nav.melosys.eessi.integration.pdl.dto.sed.adresse.Vegadresse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DnummerRekvisisjonUtenlandskVegadresse extends Vegadresse {

    private String adressenavnNummer;
    private String bygningEtasjeLeilighet;
    private String postkode;
    private String bySted;
    private String regionDistriktOmraade;
    private String landkode;
}
