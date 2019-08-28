package no.nav.melosys.eessi.models.sed.medlemskap.impl;

import org.junit.Test;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SvarAnmodningUnntakBeslutningTest {

    @Test
    public void beslutningAvslag() {
        SvarAnmodningUnntakBeslutning beslutning = SvarAnmodningUnntakBeslutning.fraRinaKode("ikke_godkjent");

        assertThat(beslutning).isEqualTo(SvarAnmodningUnntakBeslutning.AVSLAG);
        assertThat(beslutning.getRinaKode()).isEqualTo("ikke_godkjent");
    }
}
