package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PostadresseIFrittFormat {

    List<String> adresselinje;
    Poststed poststed;
}
