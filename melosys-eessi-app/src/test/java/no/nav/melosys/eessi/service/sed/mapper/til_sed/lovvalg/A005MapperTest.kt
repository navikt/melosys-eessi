package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA005
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test

class A005MapperTest {

    @Test
    fun `map til SED version 3`() {
        val sed = SedDataStub.mapTilSed<A005Mapper>(erCDM4_3 = false, testData = "mock/sedDataDtoStub.json")

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA005>()
            nav.shouldNotBeNull().arbeidsland shouldBe null
            sedVer shouldBe "2"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED version 4`() {
        val sed = SedDataStub.mapTilSed<A005Mapper>(erCDM4_3 = true, testData = "mock/sedDataDtoStub.json")

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA005>()
            nav.shouldNotBeNull().arbeidsland shouldBe null
            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }
}
