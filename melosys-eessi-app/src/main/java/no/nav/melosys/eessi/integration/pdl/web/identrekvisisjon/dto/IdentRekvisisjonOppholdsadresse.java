package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonOppholdsadresse {

    private String gyldigFraOgMed;
    private IdentRekvisisjonNorskVegadresse norskVegadresse;
    private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;
}
