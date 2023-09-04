package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
public class Postboksadresse {

    String postbokseier;
    Poststed poststed;
    String postboks;
}
