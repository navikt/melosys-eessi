package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
public class Matrikkelnummer {

    String kommunenummer;
    int gaardsnummer;
    int bruksnummer;
    Integer festenummer;
}
