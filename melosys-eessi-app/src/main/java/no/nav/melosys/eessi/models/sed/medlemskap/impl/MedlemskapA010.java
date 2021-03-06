package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg;
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland;
import no.nav.melosys.eessi.models.sed.nav.VedtakA010;

@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA010 implements Medlemskap {

    private Utsendingsland andreland;

    private VedtakA010 vedtak;

    private MeldingOmLovvalg meldingomlovvalg;
}
