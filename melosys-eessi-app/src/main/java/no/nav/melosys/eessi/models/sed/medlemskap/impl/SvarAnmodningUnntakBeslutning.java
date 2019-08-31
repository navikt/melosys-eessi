package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public enum SvarAnmodningUnntakBeslutning {

    INNVILGELSE(""),
    DELVIS_INNVILGELSE("godkjent_for_annen_periode"),
    AVSLAG("ikke_godkjent");

    private String rinaKode;

    private static final Map<String, SvarAnmodningUnntakBeslutning> rel = Collections.unmodifiableMap(
            Arrays.stream(values())
                    .collect(Collectors.toMap(SvarAnmodningUnntakBeslutning::getRinaKode, e -> e)));

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
