package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import no.nav.melosys.eessi.models.sed.nav.VedtakA010
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.Test


class MelosysEessiMeldingMapperA010Test {

    private val sedHendelse: SedHendelse = createSedHendelse()
    private val sakInformasjon: SakInformasjon = createSakInformasjon()

    @Test
    fun mapA010_fastPeriode_verifiserPeriode() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(true)).apply {
            sedType = "A010"
        }

        val melding = mapper.map(
            EessiMeldingQuery(
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
            artikkel shouldBe "11_4"
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
    fun mapA010_aapenPeriode_verifiserPeriode() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(false)).apply {
            sedType = "A010"
        }

        val melding = mapper.map(
            EessiMeldingQuery(
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
            artikkel shouldBe "11_4"
            periode.shouldNotBeNull()
                .tom shouldBe null
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
    fun mapA010_medErOpprinneligvedtak_forventAtSpesifikkRegelOverskriver() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(true)).apply {
            sedType = "A010"
            (medlemskap as MedlemskapA010).vedtak!!.eropprinneligvedtak = "nei"
        }

        val melding = mapper.map(
            EessiMeldingQuery(
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
            erEndring shouldBe true
        }
    }

    @Test
    fun mapA010_utenErOpprinneligvedtak_forventAtResultatFraEuxOverskriver() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(true)).apply {
            sedType = "A010"
        }

        val melding = mapper.map(
            EessiMeldingQuery(
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
                sedErEndring = true,
                sedVersjon = "1"
            )
        )

        melding.shouldNotBeNull().run {
            erEndring shouldBe true
        }
    }

    private fun hentMedlemskap(fastperiode: Boolean): MedlemskapA010 = MedlemskapA010().apply {
        vedtak = VedtakA010().apply {
            gjelderperiode = PeriodeA010().apply {
                if (fastperiode) {
                    sluttdato = "2019-12-01"
                    startdato = "2019-05-01"
                } else {
                    aapenperiode = AapenPeriode().apply {
                        startdato = "2019-05-01"
                    }
                }
            }
            land = "SE"
            eropprinneligvedtak = "ja"
        }
        meldingomlovvalg = MeldingOmLovvalg().apply {
            artikkel = "11_4"
        }
    }
}
