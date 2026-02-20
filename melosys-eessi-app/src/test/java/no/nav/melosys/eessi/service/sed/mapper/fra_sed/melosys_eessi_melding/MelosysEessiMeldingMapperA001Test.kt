package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.*
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Grunnlag
import no.nav.melosys.eessi.models.sed.nav.Land
import no.nav.melosys.eessi.models.sed.nav.Unntak
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperA001Test {

    private val melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory("dummy")

    private fun mapMedlemskap(medlemskap: MedlemskapA001) =
        createSedHendelse().let { hendelse ->
            melosysEessiMeldingMapperFactory.getMapper(SedType.A001).map(
                EessiMeldingParams(
                    aktoerId = "123",
                    sed = createSed(medlemskap),
                    rinaDokumentID = hendelse.rinaDokumentId,
                    rinaSaksnummer = hendelse.rinaSakId,
                    sedType = hendelse.sedType,
                    bucType = hendelse.bucType,
                    avsenderID = hendelse.avsenderId,
                    landkode = "landkode",
                    sedErEndring = false,
                    sedVersjon = "1"
                )
            )
        }

    @Test
    fun mapA001_forventRettFelt() {
        val melosysEessiMelding = mapMedlemskap(hentMedlemskapCdm43())

        melosysEessiMelding.shouldNotBeNull().run {
            artikkel shouldBe "16_1"
            lovvalgsland shouldBe "NO"
            anmodningUnntak shouldNotBe null
            anmodningUnntak?.unntakFraLovvalgsbestemmelse shouldBe "12_1"
            anmodningUnntak?.unntakFraLovvalgsland shouldBe "SE"
        }
    }

    @Test
    fun `mapA001 CDM 4_4 med forordning8832004 grunnlag`() {
        val melosysEessiMelding = mapMedlemskap(hentMedlemskapCdm44())

        melosysEessiMelding.shouldNotBeNull().run {
            artikkel shouldBe "16_1"
            lovvalgsland shouldBe "NO"
            anmodningUnntak.shouldNotBeNull().run {
                unntakFraLovvalgsbestemmelse shouldBe "16_1"
                unntakFraLovvalgsland shouldBe "SE"
                erFjernarbeidTWFA shouldBe false
            }
        }
    }

    @Test
    fun `mapA001 CDM 4_4 med TWFA markering`() {
        val medlemskap = hentMedlemskapCdm44().apply {
            rammeavtale = Rammeavtale(fjernarbeid = Fjernarbeid(eessiYesNoType = "ja"))
        }

        val melosysEessiMelding = mapMedlemskap(medlemskap)

        melosysEessiMelding.shouldNotBeNull().run {
            anmodningUnntak.shouldNotBeNull().run {
                erFjernarbeidTWFA shouldBe true
            }
        }
    }

    @Test
    fun `mapA001 CDM 4_3 fallback til unntak grunnlag`() {
        val medlemskap = hentMedlemskapCdm43()

        val melosysEessiMelding = mapMedlemskap(medlemskap)

        melosysEessiMelding.shouldNotBeNull().run {
            anmodningUnntak.shouldNotBeNull().run {
                unntakFraLovvalgsbestemmelse shouldBe "12_1"
                erFjernarbeidTWFA shouldBe false
            }
        }
    }

    @Test
    fun `mapA001 CDM 4_3 uten TWFA`() {
        val melosysEessiMelding = mapMedlemskap(hentMedlemskapCdm43())

        melosysEessiMelding.shouldNotBeNull().run {
            anmodningUnntak.shouldNotBeNull().run {
                erFjernarbeidTWFA shouldBe false
            }
        }
    }

    private fun hentMedlemskapCdm43(): MedlemskapA001 = MedlemskapA001().apply {
        soeknadsperiode = Fastperiode().apply {
            sluttdato = "2019-12-01"
            startdato = "2019-05-01"
        }
        naavaerendemedlemskap = mutableListOf(Land().apply { landkode = "SE" })
        forespurtmedlemskap = mutableListOf(Land().apply { landkode = "NO" })
        unntak = Unntak().apply {
            grunnlag = Grunnlag().apply { artikkel = "12_1" }
        }
    }

    private fun hentMedlemskapCdm44(): MedlemskapA001 = MedlemskapA001().apply {
        soeknadsperiode = Fastperiode().apply {
            sluttdato = "2019-12-01"
            startdato = "2019-05-01"
        }
        naavaerendemedlemskap = mutableListOf(Land().apply { landkode = "SE" })
        forespurtmedlemskap = mutableListOf(Land().apply { landkode = "NO" })
        unntak = Unntak().apply {
            begrunnelse = "Begrunnelse"
        }
        forordning8832004 = Forordning8832004(
            unntak = UnntakForordning(
                grunnlag = Grunnlag().apply { artikkel = "16_1" }
            )
        )
    }
}
