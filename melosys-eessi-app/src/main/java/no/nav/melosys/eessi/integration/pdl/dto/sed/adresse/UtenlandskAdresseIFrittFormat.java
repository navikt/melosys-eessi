package no.nav.melosys.eessi.integration.pdl.dto.sed.adresse;

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
