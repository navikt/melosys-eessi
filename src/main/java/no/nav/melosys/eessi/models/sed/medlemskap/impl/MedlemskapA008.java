package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.MedlemskapA008Bruker;
import no.nav.melosys.eessi.models.sed.nav.EndringA008;

@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA008 extends Medlemskap {

    private EndringA008 endring;

    private MedlemskapA008Bruker bruker;
}
