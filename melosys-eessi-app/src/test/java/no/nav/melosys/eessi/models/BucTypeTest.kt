package no.nav.melosys.eessi.models

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.jupiter.api.Test

class BucTypeTest {

    @Test
    fun bucType_hBucSomSkalKonsumeres_true() {
        BucType.erHBucsomSkalKonsumeres(BucType.H_BUC_01.name).shouldBeTrue()
    }

    @Test
    fun buctype_ikkeEksisterendeBucType_false() {
        BucType.erHBucsomSkalKonsumeres("buc").shouldBeFalse()
    }
}
