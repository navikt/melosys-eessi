package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import no.nav.melosys.eessi.models.sed.nav.VedtakA003
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MelosysEessiMeldingMapperA003Test {

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

        melosysEessiMelding.shouldNotBeNull().run {
            periode.shouldNotBeNull()
                .fom shouldBe LocalDate.of(2000, 12, 12)
            erEndring shouldBe true
            artikkel shouldBe "13_1_b_i"
        }
    }

    private fun hentMedlemskap(): MedlemskapA003 = MedlemskapA003().apply {
        vedtak = VedtakA003().apply {
            eropprinneligvedtak = IKKE_OPPRINNELIG_VEDTAK
            gjelderperiode = PeriodeA010().apply {
                startdato = "2000-12-12"
                sluttdato = "2000-12-12"
            }
        }
        relevantartikkelfor8832004eller9872009 = "13_1_b_i"
    }

    companion object {
        private val IKKE_OPPRINNELIG_VEDTAK: String? = null
    }
}
