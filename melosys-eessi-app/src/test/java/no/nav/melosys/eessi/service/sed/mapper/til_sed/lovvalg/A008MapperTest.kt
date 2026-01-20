package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test

class A008MapperTest {

    @Test
    fun `arbeid i flere land ukjent hvilke`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(testData = "mock/sedDataDtoStub.json") {
            harFastArbeidssted = false
            arbeidsland = listOf()
        }

        sed.nav.shouldNotBeNull().run {
            arbeidssted.shouldBeNull()
            harfastarbeidssted.shouldBeNull()
        }
    }

    @Test
    fun `map til SED med version 3`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(testData = "mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
        }
        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            bruker.shouldNotBeNull().run {
                arbeidiflereland.shouldNotBeNull().run {
                    yrkesaktivitet.shouldNotBeNull()
                        .startdato shouldBe "2020-01-01"
                    bosted.shouldNotBeNull()
                        .land shouldBe "SE"
                }
            }

            sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().size shouldBe 1

            sed.run {
                sedVer shouldBe "3"
                sedGVer shouldBe "4"
            }
        }
    }

    @Test
    fun `map formaal endringsmelding til SED`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(testData = "mock/sedDataDtoStub.json") {
            a008Formaal = "endringsmelding"
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal shouldBe "endringsmelding"
        }
    }

    @Test
    fun `map formaal arbeid_flere_land til SED`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(testData = "mock/sedDataDtoStub.json") {
            a008Formaal = "arbeid_flere_land"
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal shouldBe "arbeid_flere_land"
        }
    }

    @Test
    fun `formaal er null naar ikke satt`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(testData = "mock/sedDataDtoStub.json") {}

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal.shouldBeNull()
        }
    }
}
