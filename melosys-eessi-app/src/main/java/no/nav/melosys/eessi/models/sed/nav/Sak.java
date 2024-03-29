package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sak {

    private X001Anmodning anmodning;

    private Kontekst kontekst;

    private X006FjernInstitusjon fjerninstitusjon;

    private Ugyldiggjoere ugyldiggjoere;
}
