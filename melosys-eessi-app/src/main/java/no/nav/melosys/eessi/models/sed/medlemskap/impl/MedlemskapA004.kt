package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.Avslag;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA004 implements Medlemskap {

    private Avslag avslag;
}
