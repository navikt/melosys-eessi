package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Periode
import no.nav.melosys.eessi.models.sed.nav.VedtakA009
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperA009Test {

    private val sedHendelse: SedHendelse = createSedHendelse()
    private val sakInformasjon: SakInformasjon = createSakInformasjon()
    private val mapper = MelosysEessiMeldingMapperA009()

    @Test
    fun mapA009_fastPeriode_verifiserPeriode() {
        val sed = createSed(hentMedlemskap(true))
        sed.sedType = "A009"

        val melding = mapper.map(
            EessiMeldingParams(
                aktoerId = "aktørid",
                sed = sed,
                rinaDokumentID = sedHendelse.rinaDokumentId,
                rinaSaksnummer = sedHendelse.rinaSakId,
                sedType = sedHendelse.sedType,
                bucType = sedHendelse.bucType,
                avsenderID = sedHendelse.avsenderId,
                landkode = "landkode",
                journalpostID = sakInformasjon.journalpostId,
                dokumentID = sakInformasjon.dokumentId,
                gsakSaksnummer = sakInformasjon.gsakSaksnummer,
                sedErEndring = false,
                sedVersjon = "1"
            )
        )

        melding.shouldNotBeNull().run {
            gsakSaksnummer.shouldNotBeNull()
            artikkel shouldBe "12_1"
            periode.shouldNotBeNull()
                .tom.shouldNotBeNull()
            statsborgerskap.shouldNotBeEmpty()
            journalpostId shouldBe "journalpost"
            aktoerId shouldBe "aktørid"
            rinaSaksnummer shouldBe "rinasak"
            dokumentId shouldBe "dokument"
            lovvalgsland shouldBe "SE"
            gsakSaksnummer shouldBe 123L
            erEndring shouldBe false
        }
    }

    @Test
    fun mapA009_aapenPeriode_verifiserPeriode() {
        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"

        val melding = mapper.map(
            EessiMeldingParams(
                aktoerId = "aktørid",
                sed = sed,
                rinaDokumentID = sedHendelse.rinaDokumentId,
                rinaSaksnummer = sedHendelse.rinaSakId,
                sedType = sedHendelse.sedType,
                bucType = sedHendelse.bucType,
                avsenderID = sedHendelse.avsenderId,
                landkode = "landkode",
                journalpostID = sakInformasjon.journalpostId,
                dokumentID = sakInformasjon.dokumentId,
                gsakSaksnummer = sakInformasjon.gsakSaksnummer,
                sedErEndring = false,
                sedVersjon = "1"
            )
        )

        melding.shouldNotBeNull().run {
            gsakSaksnummer.shouldNotBeNull()
            artikkel shouldBe "12_1"
            periode!!.tom shouldBe null
            statsborgerskap.shouldNotBeEmpty()
            journalpostId shouldBe "journalpost"
            aktoerId shouldBe "aktørid"
            rinaSaksnummer shouldBe "rinasak"
            dokumentId shouldBe "dokument"
            lovvalgsland shouldBe "SE"
            gsakSaksnummer shouldBe 123L
            erEndring shouldBe false
        }
    }

    @Test
    fun mapA009_medEropprinneligvedtak_forventAtSpesifikkRegelOverskriver() {
        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"
        (sed.medlemskap as MedlemskapA009).vedtak!!.eropprinneligvedtak = null

        val melding = mapper.map(
            EessiMeldingParams(
                aktoerId = "123",
                sed = sed,
                rinaDokumentID = sedHendelse.rinaDokumentId,
                rinaSaksnummer = sedHendelse.rinaSakId,
                sedType = sedHendelse.sedType,
                bucType = sedHendelse.bucType,
                avsenderID = sedHendelse.avsenderId,
                landkode = "landkode",
                journalpostID = sakInformasjon.journalpostId,
                dokumentID = sakInformasjon.dokumentId,
                gsakSaksnummer = sakInformasjon.gsakSaksnummer,
                sedErEndring = false,
                sedVersjon = "1"
            )
        )

        melding.shouldNotBeNull().run {
            erEndring shouldBe true
        }
    }

    @Test
    fun mapA009_utenEropprinneligvedtak_forventAtResultatFraEuxOverskriver() {
        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"

        val melding = mapper.map(
            EessiMeldingParams(
                aktoerId = "123",
                sed = sed,
                rinaDokumentID = sedHendelse.rinaDokumentId,
                rinaSaksnummer = sedHendelse.rinaSakId,
                sedType = sedHendelse.sedType,
                bucType = sedHendelse.bucType,
                avsenderID = sedHendelse.avsenderId,
                landkode = "landkode",
                journalpostID = sakInformasjon.journalpostId,
                dokumentID = sakInformasjon.dokumentId,
                gsakSaksnummer = sakInformasjon.gsakSaksnummer,
                sedErEndring = true,
                sedVersjon = "1"
            )
        )

        melding.shouldNotBeNull().run {
            erEndring shouldBe true
        }
    }

    @Test
    fun sedErEndring_ikkeOpprinneligVedtak_forventerErEndring_true() {
        val medlemskapA009 = lagA009MedlemskapForSedErEndringTest(IKKE_OPPRINNELIG_VEDTAK)

        val erEndring = mapper.sedErEndring(medlemskapA009)

        erEndring shouldBe true
    }

    @Test
    fun sedErEndring_opprinneligVedtak_forventerErEndring_true() {
        val medlemskapA009 = lagA009MedlemskapForSedErEndringTest(OPPRINNELIG_VEDTAK)

        val erEndring = mapper.sedErEndring(medlemskapA009)

        erEndring shouldBe false
    }

    private fun lagA009MedlemskapForSedErEndringTest(opprinneligVedtak: String?): MedlemskapA009 = MedlemskapA009().apply {
        vedtak = VedtakA009().apply {
            eropprinneligvedtak = opprinneligVedtak
        }
    }

    private fun hentMedlemskap(erFastperiode: Boolean): MedlemskapA009 = MedlemskapA009().apply {
        vedtak = VedtakA009().apply {
            gjelderperiode = Periode().apply {
                if (erFastperiode) {
                    fastperiode = Fastperiode().apply {
                        sluttdato = "2019-12-01"
                        startdato = "2019-05-01"
                    }
                } else {
                    aapenperiode = AapenPeriode().apply {
                        startdato = "2019-05-01"
                    }
                }
            }
            land = "SE"
            artikkelforordning = "12_1"
            erendringsvedtak = null
            eropprinneligvedtak = "ja"
        }
    }

    companion object {
        private val IKKE_OPPRINNELIG_VEDTAK: String? = null
        private const val OPPRINNELIG_VEDTAK = "ja"
    }
}
