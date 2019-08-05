package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA002 extends Medlemskap {

    private UnntakA002 unntak;
}
