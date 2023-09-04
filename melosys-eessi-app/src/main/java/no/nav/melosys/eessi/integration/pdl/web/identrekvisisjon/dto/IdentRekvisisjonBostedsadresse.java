package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonBostedsadresse {

    private String gyldigFraOgMed;
    private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;
}
