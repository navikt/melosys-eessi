package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Periode
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.SakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSakInformasjon
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSed
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.createSedHendelse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate


class MelosysEessiMeldingMapperA002Test {

    private var sedHendelse: SedHendelse? = null
    private var sakInformasjon: SakInformasjon? = null
    private var mapper: MelosysEessiMeldingMapper? = null
    private val melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory("dummy")

    @BeforeEach
    fun setup() {
        sedHendelse = createSedHendelse()
        sakInformasjon = createSakInformasjon()
        mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.A002)
    }

    @Test
    fun mapA002_delvisInnvilget_verifiserDataSatt() {
        val sed = createSed(hentMedlemskap(false))

        val melosysEessiMelding = mapper!!.map(
            "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )

        melosysEessiMelding.shouldNotBeNull().run {
            svarAnmodningUnntak.shouldNotBeNull().run {
                beslutning shouldBe SvarAnmodningUnntakBeslutning.DELVIS_INNVILGELSE
                begrunnelse.shouldNotBeEmpty()
                delvisInnvilgetPeriode.shouldNotBeNull().run {
                    fom shouldBe LocalDate.of(2000, 12, 12)
                    tom shouldBe LocalDate.of(2000, 12, 12)
                }
            }
        }
    }

    @Test
    fun mapA002_avslag_verifiserDataSatt() {
        val sed = createSed(hentMedlemskap(true))

        val melosysEessiMelding = mapper!!.map(
            "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )

        melosysEessiMelding.shouldNotBeNull().run {
            svarAnmodningUnntak.shouldNotBeNull().run {
                beslutning shouldBe SvarAnmodningUnntakBeslutning.AVSLAG
                begrunnelse.shouldNotBeEmpty()
                delvisInnvilgetPeriode shouldBe null
            }
        }
    }

    private fun hentMedlemskap(avslag: Boolean): MedlemskapA002 = MedlemskapA002().apply {
        unntak = UnntakA002().apply {
            vedtak = VedtakA002().apply {
                if (!avslag) {
                    annenperiode = Periode().apply {
                        fastperiode = Fastperiode().apply {
                            startdato = "2000-12-12"
                            sluttdato = "2000-12-12"
                        }
                    }
                }
                resultat = if (avslag) "ikke_godkjent" else "godkjent_for_annen_periode"
                begrunnelse = "tadadada fritekst"
            }
        }
    }
}
