package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.getunleash.FakeUnleash
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.controller.dto.A008Formaal
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class A008MapperTest {

    private lateinit var fakeUnleash: FakeUnleash
    private lateinit var a008Mapper: A008Mapper

    @BeforeEach
    fun setup() {
        fakeUnleash = FakeUnleash()
        a008Mapper = A008Mapper(fakeUnleash)
    }

    @Test
    fun `arbeid i flere land ukjent hvilke`() {
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            harFastArbeidssted = false
            arbeidsland = listOf()
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.nav.shouldNotBeNull().run {
            arbeidssted.shouldBeNull()
            harfastarbeidssted.shouldBeNull()
        }
    }

    @Test
    fun `map til SED med versjon 3 naar toggle er av`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
        }

        val sed = a008Mapper.mapTilSed(sedData)

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
    fun `map til SED med versjon 4 naar toggle er paa`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)

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
                sedVer shouldBe "4"
                sedGVer shouldBe "4"
            }
        }
    }

    @Test
    fun `formaal fra sedData mappes til SED naar toggle er paa`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal shouldBe "arbeid_flere_land"
        }
    }

    @Test
    fun `formaal endringsmelding mappes korrekt`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            a008Formaal = A008Formaal.ENDRINGSMELDING
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal shouldBe "endringsmelding"
        }
    }

    @Test
    fun `kaster exception naar toggle er paa men formaal mangler`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {}

        val exception = shouldThrow<MappingException> {
            a008Mapper.mapTilSed(sedData)
        }

        exception.message shouldContain "a008Formaal er påkrevd"
    }

    @Test
    fun `formaal er null naar toggle er av selv om sedData har formaal`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal.shouldBeNull()
        }
    }
}
