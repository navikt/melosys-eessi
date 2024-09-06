package no.nav.melosys.eessi.models

import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.jupiter.api.Test

class JournalpostSedKoblingTest {

    @Test
    fun erASed_ok() {
        val journalpostSedKobling = JournalpostSedKobling(
            journalpostID = "1",
            rinaSaksnummer = "2",
            sedId = "3",
            sedVersjon = "4",
            bucType = "5",
            sedType = "A009"
        )

        journalpostSedKobling.erASed().shouldBeTrue()
    }
}

