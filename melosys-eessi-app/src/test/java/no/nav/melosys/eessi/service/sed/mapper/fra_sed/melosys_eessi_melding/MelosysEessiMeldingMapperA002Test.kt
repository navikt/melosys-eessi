package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


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

        Assertions.assertThat(melosysEessiMelding).isNotNull()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak).isNotNull()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.beslutning).isEqualTo(
            SvarAnmodningUnntakBeslutning.DELVIS_INNVILGELSE
        )
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.begrunnelse).isNotEmpty()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.delvisInnvilgetPeriode).isNotNull()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.delvisInnvilgetPeriode.fom).isEqualTo("2000-12-12")
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.delvisInnvilgetPeriode.tom).isEqualTo("2000-12-12")
    }

    @Test
    fun mapA002_avslag_verifiserDataSatt() {
        val sed = createSed(hentMedlemskap(true))

        val melosysEessiMelding = mapper!!.map(
                "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
                sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
                sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
            )

        Assertions.assertThat(melosysEessiMelding).isNotNull()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak).isNotNull()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.beslutning).isEqualTo(
            SvarAnmodningUnntakBeslutning.AVSLAG
        )
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.begrunnelse).isNotEmpty()
        Assertions.assertThat(melosysEessiMelding.svarAnmodningUnntak.delvisInnvilgetPeriode).isNull()
    }

    private fun hentMedlemskap(avslag: Boolean): MedlemskapA002 {
        val medlemskap = MedlemskapA002()

        val unntak = UnntakA002()

        val vedtak = VedtakA002()

        if (!avslag) {
            val periode = Periode()

            val fastperiode = Fastperiode()
            fastperiode.startdato = "2000-12-12"
            fastperiode.sluttdato = "2000-12-12"

            periode.fastperiode = fastperiode
            vedtak.annenperiode = periode
        }

        vedtak.resultat = if (avslag) "ikke_godkjent" else "godkjent_for_annen_periode"
        vedtak.begrunnelse = "tadadada fritekst"

        unntak.vedtak = vedtak
        medlemskap.unntak = unntak

        return medlemskap
    }
}
