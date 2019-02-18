package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland;
import no.nav.melosys.eessi.models.sed.nav.Vedtak;

@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MedlemskapA009 extends Medlemskap {

    private Utsendingsland utsendingsland;

    private Utsendingsland andreland;

    private Vedtak vedtak;
}
