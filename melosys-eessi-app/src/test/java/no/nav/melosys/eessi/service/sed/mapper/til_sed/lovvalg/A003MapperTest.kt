package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.controller.dto.VedtakDto
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test
import java.time.LocalDate

class A003MapperTest {
    private fun mapTilA003(
        erCDM4_3: Boolean,
        block: SedDataDto.() -> Unit = {}
    ): SED =
        SedDataStub.mapTilSed<A003Mapper>(erCDM4_3, "mock/sedDataDtoStub.json") {
            lovvalgsperioder.first().lovvalgsland = "NO"
            block()
        }

    @Test
    fun `map til SED med version 2`() {
        val sed = mapTilA003(false)

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA003>().run {
                andreland.shouldNotBeNull()
                    .arbeidsgiver.shouldNotBeNull().single()
                    .adresse.shouldNotBeNull()
                    .land shouldNotBe "NO"
            }
            nav.shouldNotBeNull().run {
                arbeidsgiver.shouldNotBeNull().single().adresse.shouldNotBeNull().land shouldBe "NO"
                arbeidsland shouldBe null
            }
            sedVer shouldBe "2"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED version 3`() {
        val sed = mapTilA003(true)

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA003>().run {
                andreland.shouldNotBeNull()
                    .arbeidsgiver.shouldNotBeNull().single().adresse.shouldNotBeNull().land shouldNotBe "NO"
            }
            nav.shouldNotBeNull().run {
                arbeidsgiver.shouldNotBeNull().single().adresse.shouldNotBeNull().land shouldBe "NO"
                arbeidsland.shouldNotBeNull().size shouldBe 1
            }
            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `er ikke opprinnelig vedtak forvent korrekt verdier`() {
        val sed = mapTilA003(false) {
            vedtakDto = VedtakDto(
                erFørstegangsvedtak = false,
                datoForrigeVedtak = LocalDate.now()
            )
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA003>().run {
            vedtak.shouldNotBeNull().run {
                eropprinneligvedtak shouldBe null
                erendringsvedtak shouldBe "nei"
                datoforrigevedtak.shouldNotBeNull() shouldBe LocalDate.now().toString()
            }
        }
    }

    @Test
    fun `er opprinnelig vedtak forvent korrekt verdier`() {
        val sed = mapTilA003(false) {
            vedtakDto = VedtakDto(erFørstegangsvedtak = true)
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA003>().run {
            vedtak.shouldNotBeNull().run {
                eropprinneligvedtak shouldBe "ja"
                erendringsvedtak shouldBe null
                datoforrigevedtak shouldBe null
            }
        }
    }
}
