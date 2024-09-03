package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.VedtakA009;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MelosysEessiMeldingMapperA009Test {

    private static final String IKKE_OPPRINNELIG_VEDTAK = null;
    private static final String OPPRINNELIG_VEDTAK = "ja";
    private SedHendelse sedHendelse;
    private MelosysEessiMeldingMapperStubs.SakInformasjon sakInformasjon;

    private final MelosysEessiMeldingMapperA009 mapper = new MelosysEessiMeldingMapperA009();

    @BeforeEach
    public void setup() {
        sedHendelse = createSedHendelse();
        sakInformasjon = createSakInformasjon();
    }

    @Test
    void mapA009_fastPeriode_verifiserPeriode() {
        SED sed = createSed(hentMedlemskap(true));
        sed.setSedType("A009");


        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");


        assertThat(melding).isNotNull();
        assertThat(melding.getGsakSaksnummer()).isNotNull();
        assertThat(melding.getArtikkel()).isEqualTo("12_1");
        assertThat(melding.getPeriode().getTom()).isNotNull();
        assertThat(melding.getStatsborgerskap()).isNotEmpty();
        assertThat(melding.getJournalpostId()).isEqualTo("journalpost");
        assertThat(melding.getAktoerId()).isEqualTo("aktørid");
        assertThat(melding.getRinaSaksnummer()).isEqualTo("rinasak");
        assertThat(melding.getDokumentId()).isEqualTo("dokument");
        assertThat(melding.getLovvalgsland()).isEqualTo("SE");
        assertThat(melding.getGsakSaksnummer()).isEqualTo(123L);
        assertThat(melding.isErEndring()).isFalse();
    }

    @Test
    void mapA009_aapenPeriode_verifiserPeriode() {
        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");


        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");


        assertThat(melding).isNotNull();
        assertThat(melding.getGsakSaksnummer()).isNotNull();
        assertThat(melding.getArtikkel()).isEqualTo("12_1");
        assertThat(melding.getPeriode().getTom()).isNull();
        assertThat(melding.getStatsborgerskap()).isNotEmpty();
        assertThat(melding.getJournalpostId()).isEqualTo("journalpost");
        assertThat(melding.getAktoerId()).isEqualTo("aktørid");
        assertThat(melding.getRinaSaksnummer()).isEqualTo("rinasak");
        assertThat(melding.getDokumentId()).isEqualTo("dokument");
        assertThat(melding.getLovvalgsland()).isEqualTo("SE");
        assertThat(melding.getGsakSaksnummer()).isEqualTo(123L);
        assertThat(melding.isErEndring()).isFalse();
    }

    @Test
    void mapA009_medEropprinneligvedtak_forventAtSpesifikkRegelOverskriver() {
        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");
        ((MedlemskapA009) sed.getMedlemskap()).getVedtak().setEropprinneligvedtak(null);


        MelosysEessiMelding melding = mapper.map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");


        assertThat(melding).isNotNull();
        assertThat(melding.isErEndring()).isTrue();
    }

    @Test
    void mapA009_utenEropprinneligvedtak_forventAtResultatFraEuxOverskriver() {
        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");


        MelosysEessiMelding melding = mapper.map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), true, "1");


        assertThat(melding).isNotNull();
        assertThat(melding.isErEndring()).isTrue();
    }

    @Test
    void sedErEndring_ikkeOpprinneligVedtak_forventerErEndring_true() {
        var medlemskapA009 = lagA009MedlemskapForSedErEndringTest(IKKE_OPPRINNELIG_VEDTAK);

        var erEndring = mapper.sedErEndring(medlemskapA009);

        assertTrue(erEndring);
    }

    @Test
    void sedErEndring_opprinneligVedtak_forventerErEndring_true() {
        var medlemskapA009 = lagA009MedlemskapForSedErEndringTest(OPPRINNELIG_VEDTAK);

        var erEndring = mapper.sedErEndring(medlemskapA009);

        assertFalse(erEndring);
    }

    private MedlemskapA009 lagA009MedlemskapForSedErEndringTest(String opprinneligVedtak) {
        var vedtakA009 = new VedtakA009();
        vedtakA009.setEropprinneligvedtak(opprinneligVedtak);
        var medlemskapA009 = new MedlemskapA009();
        medlemskapA009.setVedtak(vedtakA009);
        return medlemskapA009;
    }

    private MedlemskapA009 hentMedlemskap(boolean fastperiode) {
        MedlemskapA009 medlemskapA009 = new MedlemskapA009();

        VedtakA009 vedtak = new VedtakA009();
        medlemskapA009.setVedtak(vedtak);

        Periode periode = new Periode();
        if (fastperiode) {
            periode.setFastperiode(new Fastperiode());
            periode.getFastperiode().setSluttdato("2019-12-01");
            periode.getFastperiode().setStartdato("2019-05-01");
        } else {
            periode.setAapenperiode(new AapenPeriode());
            periode.getAapenperiode().setStartdato("2019-05-01");
        }
        vedtak.setGjelderperiode(periode);

        vedtak.setLand("SE");
        vedtak.setArtikkelforordning("12_1");
        vedtak.setErendringsvedtak(null);
        vedtak.setEropprinneligvedtak("ja");


        return medlemskapA009;
    }
}
