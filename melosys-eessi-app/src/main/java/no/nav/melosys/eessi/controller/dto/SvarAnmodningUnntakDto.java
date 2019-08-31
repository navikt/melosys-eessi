package no.nav.melosys.eessi.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SvarAnmodningUnntakDto {

    private SvarAnmodningUnntakBeslutning beslutning;
    private String begrunnelse;
    private Periode delvisInnvilgetPeriode;
}
