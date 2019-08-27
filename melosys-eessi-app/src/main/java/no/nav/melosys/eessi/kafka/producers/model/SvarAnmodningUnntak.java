package no.nav.melosys.eessi.kafka.producers.model;

import lombok.Data;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;

@Data
public class SvarAnmodningUnntak {

    private SvarAnmodningUnntakBeslutning beslutning;
    private String begrunnelse;
    private Periode delvisInnvilgetPeriode;
}
