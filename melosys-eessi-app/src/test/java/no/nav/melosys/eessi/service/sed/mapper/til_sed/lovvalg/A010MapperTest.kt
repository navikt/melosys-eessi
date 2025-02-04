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
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test
import java.time.LocalDate

class A010MapperTest {

    private fun mapTilA010(
        block: SedDataDto.() -> Unit = {}
    ): SED =
        SedDataStub.mapTilSed<A010Mapper>("mock/sedDataDtoStub.json") {
            lovvalgsperioder.first().apply {
                lovvalgsland = "NO"
            }
            block()
        }


    @Test
    fun `map til SED version 3`() {
        val sed = mapTilA010 {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_11_3_b
                tilleggsBestemmelse = Bestemmelse.ART_11_3_c
            }
            avklartBostedsland = "SE"
        }

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA010>()
            nav.shouldNotBeNull()
                .arbeidsland.shouldNotBeNull().shouldHaveSize(1).single()
                .land shouldBe "NO"
            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED med tilleggsbestemmelse forvent korrekt SED`() {
        val sed = mapTilA010 {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_11_3_b
                tilleggsBestemmelse = Bestemmelse.ART_11_3_c
            }
        }


        sed.shouldNotBeNull().run {
            sedType shouldBe SedType.A010.name
            medlemskap.shouldBeInstanceOf<MedlemskapA010>().run {
                meldingomlovvalg.shouldNotBeNull().artikkel shouldBe Bestemmelse.ART_11_3_b.value
                vedtak.shouldNotBeNull().gjelderperiode.shouldNotBeNull().run {
                    startdato.shouldNotBeNull()
                    sluttdato.shouldNotBeNull()
                }
                andreland.shouldNotBeNull().arbeidsgiver.shouldNotBeNull().forEach {
                    it.adresse.shouldNotBeNull().land shouldNotBe "NO"
                }
            }
        }
    }

    @Test
    fun `map til SED med ulovlig bestemmelse forvent korrekt SED`() {
        val sed = mapTilA010 {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_11_3_a
                tilleggsBestemmelse = Bestemmelse.ART_11_3_b
            }
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA010>().run {
            meldingomlovvalg.shouldNotBeNull().artikkel shouldBe Bestemmelse.ART_11_3_b.value
            vedtak.shouldNotBeNull().gjelderperiode.shouldNotBeNull().run {
                startdato.shouldNotBeNull()
                sluttdato.shouldNotBeNull()
            }
        }
    }

    @Test
    fun `map til SED med ulovlig bestemmelse forvent exception`() {
        val exception = shouldThrow<MappingException> {
            mapTilA010 {
                lovvalgsperioder.first().apply {
                    bestemmelse = Bestemmelse.ART_11_3_a
                    tilleggsBestemmelse = Bestemmelse.ART_12_1
                }
            }

        }
        exception.message.shouldContain("Kan ikke mappe til bestemmelse i A010 for lovvalgsperiode")
    }

    @Test
    fun `map til SED er ikke opprinnelig vedtak forvent korrekt verdier`() {
        val sed = mapTilA010 {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_11_3_a
                tilleggsBestemmelse = Bestemmelse.ART_11_3_b
            }
            vedtakDto = VedtakDto(
                erFørstegangsvedtak = false,
                datoForrigeVedtak = LocalDate.now()
            )
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA010>().run {
            vedtak.shouldNotBeNull().run {
                eropprinneligvedtak shouldBe null
                erendringsvedtak shouldBe "nei"
                datoforrigevedtak.shouldNotBeNull() shouldBe LocalDate.now().toString()
            }
        }
    }

    @Test
    fun `map til SED er opprinnelig vedtak forvent korrekt verdier`() {
        val sed = mapTilA010 {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_11_3_a
                tilleggsBestemmelse = Bestemmelse.ART_11_3_b
            }
            vedtakDto = VedtakDto(
                erFørstegangsvedtak = true
            )
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA010>().run {
            vedtak.shouldNotBeNull().run {
                eropprinneligvedtak shouldBe "ja"
                erendringsvedtak shouldBe null
                datoforrigevedtak shouldBe null
            }
        }
    }
}
