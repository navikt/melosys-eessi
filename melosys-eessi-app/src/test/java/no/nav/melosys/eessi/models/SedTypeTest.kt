package no.nav.melosys.eessi.models

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SedTypeTest {

    @Test
    fun kreverAdresse_verifiserer_ok() {
        val sedTypes = listOf(SedType.A001, SedType.A002, SedType.A003, SedType.A004, SedType.A007, SedType.A009, SedType.A010)
        sedTypes.forEach { sedType ->
            assertSoftly {
                withClue("SedType krever ikke adresse: '$sedType'") {
                    sedType.kreverAdresse() shouldBe true
                }
            }
        }
    }
}
