package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import no.nav.melosys.eessi.models.sed.nav.VedtakA003
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MelosysEessiMeldingMapperA003Test {
    private var sedHendelse: SedHendelse? = null
    private var sakInformasjon: SakInformasjon? = null
    private val melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory("dummy")

    @BeforeEach
    fun setup() {
        sedHendelse = createSedHendelse()
        sakInformasjon = createSakInformasjon()
    }

    @Test
    fun mapA003_verifiserDataSatt() {
        val sed = createSed(hentMedlemskap())
        val melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.A003)
            .map(
                "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
                sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
                sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
            )

        Assertions.assertThat(melosysEessiMelding).isNotNull()
        Assertions.assertThat(melosysEessiMelding.periode.fom).isEqualTo("2000-12-12")
        Assertions.assertThat(melosysEessiMelding.isErEndring).isTrue()
        Assertions.assertThat(melosysEessiMelding.artikkel).isEqualTo("13_1_b_i")
    }

    private fun hentMedlemskap(): MedlemskapA003 {
        val medlemskap = MedlemskapA003()
        medlemskap.vedtak = VedtakA003()
        medlemskap.vedtak!!.eropprinneligvedtak = IKKE_OPPRINNELIG_VEDTAK

        val periodeA010 = PeriodeA010()
        periodeA010.startdato = "2000-12-12"
        periodeA010.sluttdato = "2000-12-12"
        medlemskap.vedtak!!.gjelderperiode = periodeA010
        medlemskap.relevantartikkelfor8832004eller9872009 = "13_1_b_i"

        return medlemskap
    }

    companion object {
        private val IKKE_OPPRINNELIG_VEDTAK: String? = null
    }
}
