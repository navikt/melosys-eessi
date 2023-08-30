package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Matrikkelnummer {

    String kommunenummer;
    int gaardsnummer;
    int bruksnummer;
    Integer festenummer;
}
