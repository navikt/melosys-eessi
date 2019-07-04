package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.nav.Periode;

@Data
public class VedtakA002 {

    private Periode annenperiode;
    private String begrunnelse;
    @JsonProperty("id")
    private String resultat;
}
