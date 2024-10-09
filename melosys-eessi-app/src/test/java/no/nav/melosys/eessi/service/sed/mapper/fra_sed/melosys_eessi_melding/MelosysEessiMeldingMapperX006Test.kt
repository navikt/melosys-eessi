package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.Institusjon
import no.nav.melosys.eessi.models.sed.nav.Sak
import no.nav.melosys.eessi.models.sed.nav.X006FjernInstitusjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperX006Test {
    private var sedHendelse: SedHendelse? = null
    private var sakInformasjon: SakInformasjon? = null
    private var melosysEessiMeldingMapperFactory: MelosysEessiMeldingMapperFactory? = null

    @BeforeEach
    fun setup() {
        sedHendelse = createSedHendelse()
        sakInformasjon = createSakInformasjon()
        melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory(NORSK_INSTITUSJONS_ID)
    }

    @Test
    fun mapX006_norskInsitutsjonErMottaker_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggSatt() {
        val sed = createSed(null)
        lagNavSak(sed)

        val institusjon = lagInstitusjon(NORSK_INSTITUSJONS_ID, "NO:NAVAT07 Norge nav")

        val fjernInstitusjon = X006FjernInstitusjon()
        fjernInstitusjon.institusjon = institusjon
        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon
        val melosysEessiMelding = melosysEessiMeldingMapperFactory!!.getMapper(SedType.X006)
            .map(
                "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
                sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
                sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
            )

        Assertions.assertThat(melosysEessiMelding)
            .extracting { obj: MelosysEessiMelding -> obj.isX006NavErFjernet }
            .isEqualTo(true)
    }

    @Test
    fun mapX006_norskInsitutsjonErIkkeMottaker_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        val sed = createSed(null)
        lagNavSak(sed)
        val institusjon = lagInstitusjon("DE:DENMARK09", "DE:DENMARK09 Danmark")

        val fjernInstitusjon = X006FjernInstitusjon()
        fjernInstitusjon.institusjon = institusjon

        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon
        val melosysEessiMelding = melosysEessiMeldingMapperFactory!!.getMapper(SedType.X006)
            .map(
                "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
                sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
                sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
            )

        Assertions.assertThat(melosysEessiMelding)
            .extracting { obj: MelosysEessiMelding -> obj.isX006NavErFjernet }
            .isEqualTo(false)
    }

    @Test
    fun mapX006_institusjonIDSattNull_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        val sed = createSed(null)
        lagNavSak(sed)
        val institusjon = lagInstitusjon(null, null)

        val fjernInstitusjon = X006FjernInstitusjon()
        fjernInstitusjon.institusjon = institusjon

        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon
        val melosysEessiMelding = melosysEessiMeldingMapperFactory!!.getMapper(SedType.X006)
            .map(
                "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
                sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
                sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
            )

        Assertions.assertThat(melosysEessiMelding)
            .extracting { obj: MelosysEessiMelding -> obj.isX006NavErFjernet }
            .isEqualTo(false)
    }

    @Test
    fun mapX006_institusjonIDSattTom_oppdatererMelosysEessiMeldingOpprettetMedInstitusjonFlaggIkkeSatt() {
        val sed = createSed(null)
        lagNavSak(sed)
        val institusjon = lagInstitusjon("", "")

        val fjernInstitusjon = X006FjernInstitusjon()
        fjernInstitusjon.institusjon = institusjon

        sed.nav!!.sak!!.fjerninstitusjon = fjernInstitusjon
        val melosysEessiMelding = melosysEessiMeldingMapperFactory!!.getMapper(SedType.X006)
            .map(
                "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
                sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
                sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
            )

        Assertions.assertThat(melosysEessiMelding)
            .extracting { obj: MelosysEessiMelding -> obj.isX006NavErFjernet }
            .isEqualTo(false)
    }

    private fun lagInstitusjon(id: String?, navn: String?): Institusjon {
        val institusjon = Institusjon()
        institusjon.id = id
        institusjon.navn = navn
        return institusjon
    }

    private fun lagNavSak(sed: SED) {
        if (sed.nav != null) {
            sed.nav!!.sak = Sak()
        }
    }

    companion object {
        private const val NORSK_INSTITUSJONS_ID = "NO:NAVAT07"
    }
}
