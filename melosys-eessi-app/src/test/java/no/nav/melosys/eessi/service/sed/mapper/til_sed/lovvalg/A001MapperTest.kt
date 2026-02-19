package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.getunleash.FakeUnleash
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class A001MapperTest {

    private lateinit var fakeUnleash: FakeUnleash
    private lateinit var a001Mapper: A001Mapper

    @BeforeEach
    fun setup() {
        fakeUnleash = FakeUnleash()
        a001Mapper = A001Mapper(fakeUnleash)
    }

    private fun lagSedData(block: SedDataDto.() -> Unit = {}): SedDataDto =
        SedDataStub.getStub("mock/sedDataDtoStub.json") {
            lovvalgsperioder.first().apply {
                bestemmelse = Bestemmelse.ART_16_1
                fom = LocalDate.now()
                tom = LocalDate.now().plusYears(1)
                lovvalgsland = "NO"
                unntakFraLovvalgsland = "SE"
                unntakFraBestemmelse = Bestemmelse.ART_16_1
            }
            block()
        }

    private fun mapTilA001(block: SedDataDto.() -> Unit = {}): SED =
        a001Mapper.mapTilSed(lagSedData(block))

    @Test
    fun `map til SED med versjon 3 naar CDM 4_4 toggle er av`() {
        fakeUnleash.disable(CDM_4_4)

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

    @Test
    fun `map til SED med versjon 4 naar CDM 4_4 toggle er paa`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001()

        sed.run {
            sedVer shouldBe "4"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `grunnlag settes i forordning8832004 naar CDM 4_4`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001()

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            forordning8832004.shouldNotBeNull().run {
                unntak.shouldNotBeNull()
                    .grunnlag.shouldNotBeNull()
                    .artikkel shouldBe "16_1"
            }
        }
    }

    @Test
    fun `unntak grunnlag er null i CDM 4_4`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001()

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            unntak.shouldNotBeNull().run {
                grunnlag.shouldBeNull()
                begrunnelse.shouldBeNull() // begrunnelse er null i stub-data
            }
        }
    }

    @Test
    fun `unntak grunnlag settes i CDM 4_3`() {
        fakeUnleash.disable(CDM_4_4)

        val sed = mapTilA001()

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            unntak.shouldNotBeNull()
                .grunnlag.shouldNotBeNull()
                .artikkel shouldBe "16_1"

            forordning8832004.shouldBeNull()
        }
    }

    @Test
    fun `TWFA settes til ja naar erFjernarbeidTWFA er true og artikkel er 13_1_a`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001 {
            erFjernarbeidTWFA = true
            lovvalgsperioder.first().unntakFraBestemmelse = Bestemmelse.ART_13_1_a
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            rammeavtale.shouldNotBeNull()
                .fjernarbeid.shouldNotBeNull()
                .eessiYesNoType shouldBe "ja"
        }
    }

    @Test
    fun `TWFA settes til nei naar erFjernarbeidTWFA er false og artikkel er 13_1_a`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001 {
            erFjernarbeidTWFA = false
            lovvalgsperioder.first().unntakFraBestemmelse = Bestemmelse.ART_13_1_a
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            rammeavtale.shouldNotBeNull()
                .fjernarbeid.shouldNotBeNull()
                .eessiYesNoType shouldBe "nei"
        }
    }

    @Test
    fun `TWFA settes til nei naar erFjernarbeidTWFA er null og artikkel er 13_1_a`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001 {
            erFjernarbeidTWFA = null
            lovvalgsperioder.first().unntakFraBestemmelse = Bestemmelse.ART_13_1_a
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            rammeavtale.shouldNotBeNull()
                .fjernarbeid.shouldNotBeNull()
                .eessiYesNoType shouldBe "nei"
        }
    }

    @Test
    fun `rammeavtale er null naar artikkel ikke er 13_1_a selv med CDM 4_4`() {
        fakeUnleash.enable(CDM_4_4)

        val sed = mapTilA001 { erFjernarbeidTWFA = true }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            rammeavtale.shouldBeNull()
        }
    }

    @Test
    fun `TWFA ignoreres naar CDM 4_3 selv om erFjernarbeidTWFA er true`() {
        fakeUnleash.disable(CDM_4_4)

        val sed = mapTilA001 {
            erFjernarbeidTWFA = true
            lovvalgsperioder.first().unntakFraBestemmelse = Bestemmelse.ART_13_1_a
        }

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA001>().run {
            forordning8832004.shouldBeNull()
            rammeavtale.shouldBeNull()
        }
    }
}
