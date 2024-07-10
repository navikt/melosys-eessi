package no.nav.melosys.eessi.models

import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.jupiter.api.Test

class JournalpostSedKoblingTest {

    @Test
    fun erASed_ok() {
        val journalpostSedKobling = JournalpostSedKobling().apply {
            sedType = "A009"
        }

        journalpostSedKobling.erASed().shouldBeTrue()
    }
}

