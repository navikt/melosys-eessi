package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import java.util.Map;
import com.google.common.collect.ImmutableMap;

public enum SvarAnmodningUnntakBeslutning {

    INNVILGELSE(""),
    DELVIS_INNVILGELSE("godkjent_for_annen_periode"),
    AVSLAG("ikke_godkjent");

    private String rinaKode;

    private static final Map<String, SvarAnmodningUnntakBeslutning> rel = ImmutableMap.of(
            INNVILGELSE.rinaKode, INNVILGELSE,
            DELVIS_INNVILGELSE.rinaKode, DELVIS_INNVILGELSE,
            AVSLAG.rinaKode, AVSLAG
    );

    SvarAnmodningUnntakBeslutning(String rinaKode) {
        this.rinaKode = rinaKode;
    }

    public String getRinaKode() {
        return rinaKode;
    }

    public static SvarAnmodningUnntakBeslutning fraRinaKode(String rinaKode) {
        return rel.get(rinaKode);
    }
}
