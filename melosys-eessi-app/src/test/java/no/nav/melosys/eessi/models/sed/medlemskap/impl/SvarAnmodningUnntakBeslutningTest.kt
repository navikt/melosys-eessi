package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SvarAnmodningUnntakBeslutningTest {

    @Test
    void beslutningAvslag() {
        SvarAnmodningUnntakBeslutning beslutning = SvarAnmodningUnntakBeslutning.fraRinaKode("ikke_godkjent");

        assertThat(beslutning).isEqualTo(SvarAnmodningUnntakBeslutning.AVSLAG);
        assertThat(beslutning.getRinaKode()).isEqualTo("ikke_godkjent");
    }
}
