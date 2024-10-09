package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class MelosysEessiMeldingMapperA010Test {
    private var sedHendelse: SedHendelse? = null
    private var sakInformasjon: SakInformasjon? = null

    @BeforeEach
    fun setup() {
        sedHendelse = createSedHendelse()
        sakInformasjon = createSakInformasjon()
    }

    @Test
    fun mapA010_fastPeriode_verifiserPeriode() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(true))
        sed.sedType = "A010"
        val melding = mapper.map(
            "aktørid", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )

        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.gsakSaksnummer).isNotNull()
        Assertions.assertThat(melding.artikkel).isEqualTo("11_4")
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
    fun mapA010_aapenPeriode_verifiserPeriode() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(false))
        sed.sedType = "A009"
        val melding = mapper.map(
            "aktørid", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )

        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.gsakSaksnummer).isNotNull()
        Assertions.assertThat(melding.artikkel).isEqualTo("11_4")
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
    fun mapA010_medErOpprinneligvedtak_forventAtSpesifikkRegelOverskriver() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(true))
        sed.sedType = "A010"
        (sed.medlemskap as MedlemskapA010).vedtak!!.eropprinneligvedtak = "nei"
        val melding = mapper.map(
            "aktørid", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, false, "1"
        )

        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.isErEndring).isTrue()
    }

    @Test
    fun mapA010_utenErOpprinneligvedtak_forventAtResultatFraEuxOverskriver() {
        val mapper: MelosysEessiMeldingMapper = MelosysEessiMeldingMapperA010()

        val sed = createSed(hentMedlemskap(true))
        sed.sedType = "A010"
        val melding = mapper.map(
            "aktørid", sed, sedHendelse!!.rinaDokumentId, sedHendelse!!.rinaSakId,
            sedHendelse!!.sedType, sedHendelse!!.bucType, sedHendelse!!.avsenderId, "landkode", sakInformasjon!!.journalpostId,
            sakInformasjon!!.dokumentId, sakInformasjon!!.gsakSaksnummer, true, "1"
        )

        Assertions.assertThat(melding).isNotNull()
        Assertions.assertThat(melding.isErEndring).isTrue()
    }

    private fun hentMedlemskap(fastperiode: Boolean): MedlemskapA010 {
        val medlemskapA010 = MedlemskapA010()

        val vedtak = VedtakA010()
        medlemskapA010.vedtak = vedtak

        val periode = PeriodeA010()
        if (fastperiode) {
            periode.sluttdato = "2019-12-01"
            periode.startdato = "2019-05-01"
        } else {
            periode.aapenperiode = AapenPeriode()
            periode.aapenperiode!!.startdato = "2019-05-01"
        }
        vedtak.gjelderperiode = periode

        vedtak.land = "SE"
        vedtak.eropprinneligvedtak = "ja"

        medlemskapA010.meldingomlovvalg = MeldingOmLovvalg()
        medlemskapA010.meldingomlovvalg!!.artikkel = "11_4"

        return medlemskapA010
    }
}
