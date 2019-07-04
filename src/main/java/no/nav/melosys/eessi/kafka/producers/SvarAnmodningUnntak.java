package no.nav.melosys.eessi.kafka.producers;

import lombok.Data;

@Data
public class SvarAnmodningUnntak {

    private Beslutning beslutning;
    private String begrunnelse;
    private Periode delvisInnvilgetPeriode;

    public enum Beslutning {
        INNVILGELSE, DELVIS_INNVILGELSE, AVSLAG;
    }
}
