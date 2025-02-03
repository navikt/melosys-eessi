package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.controller.dto.VedtakDto
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test
import java.time.LocalDate

class A009MapperTest {

    private val lovvalgsland = "NO"

    private fun mapTilA009(
        block: SedDataDto.() -> Unit = {}
    ): SED =
        SedDataStub.mapTilSed<A009Mapper>( "mock/sedDataDtoStub.json") {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_12_1
                fom = LocalDate.now()
                tom = LocalDate.now().plusYears(1)
                lovvalgsland = this@A009MapperTest.lovvalgsland
            }
            block()
        }

    @Test
    fun `map til SED version 3`() {
        val sed = mapTilA009 {
            avklartBostedsland = "SE"
        }

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA009>()

            nav.shouldNotBeNull()
                .arbeidsland.shouldNotBeNull().shouldHaveSize(1).single()
                .land shouldBe lovvalgsland

            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `medlemskap ikke selvstendig og artikkel 12_1 forvent gyldig medlemskap`() {
        val sed = mapTilA009()

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA009>().run {
            utsendingsland.shouldNotBeNull().arbeidsgiver.shouldNotBeNull()
                .shouldHaveSize(1).single()
                .adresse.shouldNotBeNull()
                .land shouldBe lovvalgsland

            andreland.shouldNotBeNull()
                .arbeidsgiver.shouldNotBeNull()
                .shouldHaveSize(1).single()
                .adresse.shouldNotBeNull()
                .land shouldNotBe lovvalgsland

            vedtak.shouldNotBeNull().run {
                artikkelforordning shouldBe "12_1"
                gjelderperiode.shouldNotBeNull().fastperiode.shouldNotBeNull()
                gjelderperiode.shouldNotBeNull().aapenperiode shouldBe null
            }
        }
    }

    @Test
    fun `er ikke opprinnelig vedtak forvent korrekt verdier`() {
        val sed = mapTilA009() {
            vedtakDto = VedtakDto(
                erFørstegangsvedtak = false,
                datoForrigeVedtak = LocalDate.now()

            )
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA009>().run {
            vedtak.shouldNotBeNull().run {
                eropprinneligvedtak shouldBe null
                erendringsvedtak shouldBe "nei"
                datoforrigevedtak.shouldNotBeNull() shouldBe LocalDate.now().toString()
            }
        }
    }

    @Test
    fun `er opprinnelig vedtak forvent korrekt verdier`() {
        val sed = mapTilA009() {
            vedtakDto = VedtakDto(erFørstegangsvedtak = true)
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA009>().run {
            vedtak.shouldNotBeNull().run {
                eropprinneligvedtak shouldBe "ja"
                erendringsvedtak shouldBe null
                datoforrigevedtak shouldBe null
            }
        }
    }

    @Test
    fun `medlemskap er selvstendig og artikkel 12_2 forvent gyldig medlemskap`() {
        val sed = mapTilA009() {
            lovvalgsperioder.first().bestemmelse = Bestemmelse.ART_12_2
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA009>().run {
            vedtak.shouldNotBeNull().run {
                artikkelforordning shouldBe "12_2"
                gjelderperiode.shouldNotBeNull().fastperiode.shouldNotBeNull()
                gjelderperiode.shouldNotBeNull().aapenperiode shouldBe null
            }
        }
    }

    @Test
    fun `medlemskap feil lovvalgs bestemmelse forvent MappingException`() {
        val exception = shouldThrow<MappingException> {
            mapTilA009() {
                lovvalgsperioder.first().bestemmelse = Bestemmelse.ART_13_4
            }
        }
        exception.message.shouldContain("Lovvalgsbestemmelse er ikke av artikkel 12!")
    }

    @Test
    fun `er ikke fysisk forvent er ikke fastadresse`() {
        val sed = mapTilA009() {
            arbeidsland.first().arbeidssted.first().fysisk = false
        }

        sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().first().arbeidssted.first().erikkefastadresse shouldBe "ja"
    }
}
