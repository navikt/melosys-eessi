package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonKontaktadresse {

    private IdentRekvisisjonNorskVegadresse norskVegadresse;
    private IdentRekvisisjonNorskPostboksadresse norskPostboksadresse;
    private IdentRekvisisjonUtenlandskVegadresse utenlandskVegadresse;
    private IdentRekvisisjonUtenlandskPostboksadresse utenlandskPostboksadresse;
}
