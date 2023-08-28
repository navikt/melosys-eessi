package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Poststed {

    String poststedsnavn;
    String postnummer;
}
