package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MelosysEessiMeldingMapperA009Test {
    private var sedHendelse: SedHendelse? = null
    private var sakInformasjon: SakInformasjon? = null

    private val mapper = MelosysEessiMeldingMapperA009()

    @BeforeEach
    fun setup() {
        sedHendelse = createSedHendelse()
        sakInformasjon = createSakInformasjon()
    }

    @Test
    fun mapA009_fastPeriode_verifiserPeriode() {
        val sed = createSed(hentMedlemskap(true))
        sed.sedType = "A009"


        val melding = mapper.map(
            "aktørid", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )


        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.gsakSaksnummer).isNotNull()
        Assertions.assertThat(melding.artikkel).isEqualTo("12_1")
        Assertions.assertThat(melding.periode.tom).isNotNull()
        Assertions.assertThat(melding.statsborgerskap).isNotEmpty()
        Assertions.assertThat(melding.journalpostId).isEqualTo("journalpost")
        Assertions.assertThat(melding.aktoerId).isEqualTo("aktørid")
        Assertions.assertThat(melding.rinaSaksnummer).isEqualTo("rinasak")
        Assertions.assertThat(melding.dokumentId).isEqualTo("dokument")
        Assertions.assertThat(melding.lovvalgsland).isEqualTo("SE")
        Assertions.assertThat(melding.gsakSaksnummer).isEqualTo(123L)
        Assertions.assertThat(melding.isErEndring).isFalse()
    }

    @Test
    fun mapA009_aapenPeriode_verifiserPeriode() {
        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"


        val melding = mapper.map(
            "aktørid", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )


        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.gsakSaksnummer).isNotNull()
        Assertions.assertThat(melding.artikkel).isEqualTo("12_1")
        Assertions.assertThat(melding.periode.tom).isNull()
        Assertions.assertThat(melding.statsborgerskap).isNotEmpty()
        Assertions.assertThat(melding.journalpostId).isEqualTo("journalpost")
        Assertions.assertThat(melding.aktoerId).isEqualTo("aktørid")
        Assertions.assertThat(melding.rinaSaksnummer).isEqualTo("rinasak")
        Assertions.assertThat(melding.dokumentId).isEqualTo("dokument")
        Assertions.assertThat(melding.lovvalgsland).isEqualTo("SE")
        Assertions.assertThat(melding.gsakSaksnummer).isEqualTo(123L)
        Assertions.assertThat(melding.isErEndring).isFalse()
    }

    @Test
    fun mapA009_medEropprinneligvedtak_forventAtSpesifikkRegelOverskriver() {
        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"
        (sed.medlemskap as MedlemskapA009).vedtak!!.eropprinneligvedtak = null


        val melding = mapper.map(
            "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )


        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.isErEndring).isTrue()
    }

    @Test
    fun mapA009_utenEropprinneligvedtak_forventAtResultatFraEuxOverskriver() {
        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"


        val melding = mapper.map(
            "123", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, true, "1"
        )


        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.isErEndring).isTrue()
    }

    @Test
    fun sedErEndring_ikkeOpprinneligVedtak_forventerErEndring_true() {
        val medlemskapA009 = lagA009MedlemskapForSedErEndringTest(IKKE_OPPRINNELIG_VEDTAK)

        val erEndring = mapper.sedErEndring(medlemskapA009)

        org.junit.jupiter.api.Assertions.assertTrue(erEndring)
    }

    @Test
    fun sedErEndring_opprinneligVedtak_forventerErEndring_true() {
        val medlemskapA009 = lagA009MedlemskapForSedErEndringTest(OPPRINNELIG_VEDTAK)

        val erEndring = mapper.sedErEndring(medlemskapA009)

        org.junit.jupiter.api.Assertions.assertFalse(erEndring)
    }

    private fun lagA009MedlemskapForSedErEndringTest(opprinneligVedtak: String?): MedlemskapA009 {
        val vedtakA009 = VedtakA009()
        vedtakA009.eropprinneligvedtak = opprinneligVedtak
        val medlemskapA009 = MedlemskapA009()
        medlemskapA009.vedtak = vedtakA009
        return medlemskapA009
    }

    private fun hentMedlemskap(fastperiode: Boolean): MedlemskapA009 {
        val medlemskapA009 = MedlemskapA009()

        val vedtak = VedtakA009()
        medlemskapA009.vedtak = vedtak

        val periode = Periode()
        if (fastperiode) {
            periode.fastperiode = Fastperiode()
            periode.fastperiode!!.sluttdato = "2019-12-01"
            periode.fastperiode!!.startdato = "2019-05-01"
        } else {
            periode.aapenperiode = AapenPeriode()
            periode.aapenperiode!!.startdato = "2019-05-01"
        }
        vedtak.gjelderperiode = periode

        vedtak.land = "SE"
        vedtak.artikkelforordning = "12_1"
        vedtak.erendringsvedtak = null
        vedtak.eropprinneligvedtak = "ja"


        return medlemskapA009
    }

    companion object {
        private val IKKE_OPPRINNELIG_VEDTAK: String? = null
        private const val OPPRINNELIG_VEDTAK = "ja"
    }
}
