package no.nav.melosys.eessi.config.observability

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TraceparentTest {

    @Test
    fun `uten aktiv trace er det ingen traceparent`() {
        Traceparent.nåværende().shouldBeNull()
    }

    @Test
    fun `aktiver gjenoppretter lagret traceparent som gjeldende kontekst`() {
        Traceparent.aktiver(TRACEPARENT).use {
            Traceparent.nåværende() shouldBe TRACEPARENT
        }
        Traceparent.nåværende().shouldBeNull()
    }

    @Test
    fun `aktiver uten traceparent endrer ikke konteksten`() {
        Traceparent.aktiver(null).use {
            Traceparent.nåværende().shouldBeNull()
        }
    }

    companion object {
        private const val TRACEPARENT = "00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01"
    }
}
