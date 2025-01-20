package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.controller.dto.UtpekingAvvisDto
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA004
import no.nav.melosys.eessi.models.sed.nav.Avslag
import no.nav.melosys.eessi.models.sed.nav.Land
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test

class A004MapperTest {
    @Test
    fun `map til SED med version 2`() {
        val sed = SedDataStub.mapTilSed<A004Mapper>(erCDM4_3 = false, testData = "mock/sedDataDtoStub.json") {
            utpekingAvvis = UtpekingAvvisDto(
                nyttLovvalgsland = "DK",
                begrunnelseUtenlandskMyndighet = "begrunnelse",
                vilSendeAnmodningOmMerInformasjon = false
            )
        }

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA004>()
            nav.shouldNotBeNull().arbeidsland shouldBe null
            sedVer shouldBe "2"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED version 3`() {
        val sed = SedDataStub.mapTilSed<A004Mapper>(erCDM4_3 = true, testData = "mock/sedDataDtoStub.json") {
            utpekingAvvis = UtpekingAvvisDto(
                nyttLovvalgsland = "DK",
                begrunnelseUtenlandskMyndighet = "begrunnelse",
                vilSendeAnmodningOmMerInformasjon = false
            )
        }

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA004>()
            nav.shouldNotBeNull().arbeidsland shouldBe null
            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED forvent MedlemskapA004`() {
        val sed = SedDataStub.mapTilSed<A004Mapper>(erCDM4_3 = false, testData = "mock/sedDataDtoStub.json") {
            utpekingAvvis = UtpekingAvvisDto(
                nyttLovvalgsland = "DK",
                begrunnelseUtenlandskMyndighet = "begrunnelse",
                vilSendeAnmodningOmMerInformasjon = false
            )
        }

        sed.shouldNotBeNull()
            .medlemskap.shouldBeInstanceOf<MedlemskapA004>()
            .avslag shouldBe Avslag(
            begrunnelse = "begrunnelse",
            erbehovformerinformasjon = "nei",
            forslagformedlemskap = Land("DK")
        )
    }

    @Test
    fun `map til SED uten UtpekingAvvis forvent Exception`() {
        val exception = shouldThrow<MappingException> {
            SedDataStub.mapTilSed<A004Mapper>(erCDM4_3 = false, testData = "mock/sedDataDtoStub.json")
        }
        exception.message.shouldNotBeNull().shouldContain("Trenger UtpekingAvvis for å opprette A004")
    }
}
