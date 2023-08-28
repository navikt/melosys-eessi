package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PostadresseIFrittFormat {

    List<String> adresselinje;
    Poststed poststed;
}
