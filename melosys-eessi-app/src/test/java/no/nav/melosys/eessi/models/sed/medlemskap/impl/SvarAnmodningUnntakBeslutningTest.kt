package no.nav.melosys.eessi.models.sed.medlemskap.impl

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SvarAnmodningUnntakBeslutningTest {
    @Test
    fun beslutningAvslag() {
        val beslutning: SvarAnmodningUnntakBeslutning? = SvarAnmodningUnntakBeslutning.fraRinaKode("ikke_godkjent")

        beslutning.shouldNotBeNull().run {
            this shouldBe SvarAnmodningUnntakBeslutning.AVSLAG
            rinaKode shouldBe "ikke_godkjent"
        }
    }
}
