package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
public class Postboksadresse {

    String postbokseier;
    Poststed poststed;
    String postboks;
}
