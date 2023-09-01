package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse.Vegadresse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdentRekvisisjonUtenlandskVegadresse extends Vegadresse {

    private String adressenavnNummer;
    private String bygningEtasjeLeilighet;
    private String postkode;
    private String bySted;
    private String regionDistriktOmraade;
    private String landkode;
}
