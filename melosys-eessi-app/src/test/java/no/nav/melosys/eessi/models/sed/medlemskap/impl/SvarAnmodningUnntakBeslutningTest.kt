package no.nav.melosys.eessi.models.sed.medlemskap.impl

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class SvarAnmodningUnntakBeslutningTest {
    @Test
    fun beslutningAvslag() {
        val beslutning: SvarAnmodningUnntakBeslutning? = SvarAnmodningUnntakBeslutning.fraRinaKode("ikke_godkjent")

        Assertions.assertThat(beslutning).isEqualTo(SvarAnmodningUnntakBeslutning.AVSLAG)
        Assertions.assertThat(beslutning!!.rinaKode).isEqualTo("ikke_godkjent")
    }
}
