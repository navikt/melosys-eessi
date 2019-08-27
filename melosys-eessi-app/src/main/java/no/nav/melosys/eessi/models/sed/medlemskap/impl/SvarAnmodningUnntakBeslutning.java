package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import java.util.Arrays;

public enum SvarAnmodningUnntakBeslutning {

    INNVILGELSE(""),
    DELVIS_INNVILGELSE("godkjent_for_annen_periode"),
    AVSLAG("ikke_godkjent");

    private String rinaKode;

    SvarAnmodningUnntakBeslutning(String rinaKode) {
        this.rinaKode = rinaKode;
    }

    public String getRinaKode() {
        return rinaKode;
    }

    public static SvarAnmodningUnntakBeslutning fraRinaKode(String rinaKode) {
        return Arrays.stream(SvarAnmodningUnntakBeslutning.values())
                .filter(beslutning -> beslutning.getRinaKode().equalsIgnoreCase(rinaKode))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Finner ikke beslutning med rinaKode " + rinaKode));
    }
}
