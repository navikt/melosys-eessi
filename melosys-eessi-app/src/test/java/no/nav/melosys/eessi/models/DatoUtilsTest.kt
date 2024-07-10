package no.nav.melosys.eessi.models

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.DatoUtils.tilLocalDate
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DatoUtilsTest {

    @Test
    fun tilLocalDate_stringMedTime_parserTilLocaldate() {
        val input = "2020-01-01:+02:00"
        val forventet = LocalDate.of(2020, 1, 1)

        tilLocalDate(input) shouldBe forventet
    }
}
