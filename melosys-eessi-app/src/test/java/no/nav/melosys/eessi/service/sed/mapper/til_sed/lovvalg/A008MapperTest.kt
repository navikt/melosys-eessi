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
        val sed = SedDataStub.mapTilSed<A008Mapper>(erCDM4_3 = true, testData = "mock/sedDataDtoStub.json") {
            harFastArbeidssted = false
            arbeidsland = listOf()
        }

        sed.nav.shouldNotBeNull().run {
            arbeidssted.shouldBeNull()
            harfastarbeidssted.shouldBeNull()
        }
    }

    @Test
    fun `map til SED med version 2`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(erCDM4_3 = false, testData = "mock/sedDataDtoStub.json") {
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
        }

        sed.nav.shouldNotBeNull().arbeidsland.shouldBeNull()

        sed.run {
            sedVer shouldBe "2"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED med version 3`() {
        val sed = SedDataStub.mapTilSed<A008Mapper>(erCDM4_3 = true, testData = "mock/sedDataDtoStub.json") {
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
}
