package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.Andreland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA003;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedlemskapA003 implements Medlemskap {

    private List<String> gjeldendereglerEC883;

    private String relevantartikkelfor8832004eller9872009;

    private Andreland andreland;

    private VedtakA003 vedtak;

    private String isDeterminationProvisional;
}
