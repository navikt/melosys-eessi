package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.adresse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostadresseIFrittFormat {

    List<String> adresselinje;
    Poststed poststed;
}
