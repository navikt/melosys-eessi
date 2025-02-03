package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test
import java.time.LocalDate

class A001MapperTest {
    private fun mapTilA001(): SED {
        return SedDataStub.mapTilSed<A001Mapper>(testData = "mock/sedDataDtoStub.json") {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_16_1
                fom = LocalDate.now()
                tom = LocalDate.now().plusYears(1)
                lovvalgsland = "NO"
                unntakFraLovvalgsland = "SE"
                unntakFraBestemmelse = Bestemmelse.ART_16_1
            }
        }
    }

    @Test
    fun `map til SED med version 3`() {
        val sed = mapTilA001()

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            vertsland.shouldNotBeNull()
                .arbeidsgiver.shouldNotBeNull().single()
                .adresse.shouldNotBeNull()
                .land shouldNotBe "NO"

            naavaerendemedlemskap.shouldNotBeNull().single().shouldNotBeNull()
                .landkode shouldBe "SE"

            forespurtmedlemskap.shouldNotBeNull().single().shouldNotBeNull()
                .landkode shouldBe "NO"
        }

        sed.nav.shouldNotBeNull().run {
            arbeidsgiver.shouldNotBeNull().single()
                .adresse.shouldNotBeNull()
                .land shouldBe "NO"
            arbeidsland.shouldNotBeNull().shouldHaveSize(1).single().land shouldBe "NO"
        }

        sed.run {
            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }
}
