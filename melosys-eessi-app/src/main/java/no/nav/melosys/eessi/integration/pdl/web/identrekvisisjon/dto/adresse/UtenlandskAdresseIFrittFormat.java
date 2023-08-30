package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UtenlandskAdresseIFrittFormat {

    List<String> adresselinje;
    String postkode;
    String byEllerStedsnavn;
    String landkode;
}
