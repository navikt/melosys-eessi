package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.Institusjon
import no.nav.melosys.eessi.models.sed.nav.Sak
import no.nav.melosys.eessi.models.sed.nav.X006FjernInstitusjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperX006Test {

    private lateinit var sedHendelse: SedHendelse
    private lateinit var sakInformasjon: SakInformasjon
    private lateinit var melosysEessiMeldingMapperFactory: MelosysEessiMeldingMapperFactory

    @BeforeEach
    fun setup() {
        sedHendelse = createSedHendelse()
        sakInformasjon = createSakInformasjon()
        melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory(NORSK_INSTITUSJONS_ID)
    }

    @Test
    fun mapX006_norskInstitusjonErMottaker_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggSatt() {
        val sed = createSed(null).apply { lagNavSak(this) }
        val institusjon = lagInstitusjon(NORSK_INSTITUSJONS_ID, "NO:NAVAT07 Norge nav")

        val fjernInstitusjon = X006FjernInstitusjon().apply { this.institusjon = institusjon }
        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon

        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006).map(
            EessiMeldingQuery(
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

        melosysEessiMelding.shouldNotBeNull().apply {
            x006NavErFjernet shouldBe true
        }
    }

    @Test
    fun mapX006_norskInstitusjonErIkkeMottaker_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        val sed = createSed(null).apply { lagNavSak(this) }
        val institusjon = lagInstitusjon("DE:DENMARK09", "DE:DENMARK09 Danmark")

        val fjernInstitusjon = X006FjernInstitusjon().apply { this.institusjon = institusjon }
        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon

        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006).map(
            EessiMeldingQuery(
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

        melosysEessiMelding.shouldNotBeNull().apply {
            x006NavErFjernet shouldBe false
        }
    }

    @Test
    fun mapX006_institusjonIDSattNull_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        val sed = createSed(null).apply { lagNavSak(this) }
        val institusjon = lagInstitusjon(null, null)

        val fjernInstitusjon = X006FjernInstitusjon().apply { this.institusjon = institusjon }
        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon

        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006).map(
            EessiMeldingQuery(
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

        melosysEessiMelding.shouldNotBeNull().apply {
            x006NavErFjernet shouldBe false
        }
    }

    @Test
    fun mapX006_institusjonIDSattTom_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        val sed = createSed(null).apply { lagNavSak(this) }
        val institusjon = lagInstitusjon("", "")

        val fjernInstitusjon = X006FjernInstitusjon().apply { this.institusjon = institusjon }
        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon

        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.X006).map(
            EessiMeldingQuery(
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

        melosysEessiMelding.shouldNotBeNull().apply {
            x006NavErFjernet shouldBe false
        }
    }

    private fun lagInstitusjon(id: String?, navn: String?): Institusjon = Institusjon().apply {
        this.id = id
        this.navn = navn
    }

    private fun lagNavSak(sed: SED) {
        sed.nav?.apply {
            sak = Sak()
        }
    }

    companion object {
        private const val NORSK_INSTITUSJONS_ID = "NO:NAVAT07"
    }
}
