package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.models.sed.nav.VedtakA009;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.junit.Before;
import org.junit.Test;

import static no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperA009Test {

    private SedHendelse sedHendelse;
    private SakInformasjon sakInformasjon;

    @Before
    public void setup() {
        sedHendelse = createSedHendelse();
        sakInformasjon = createSakInformasjon();
    }

    @Test
    public void mapA009_fastPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA009();

        SED sed = createSed(hentMedlemskap(true));
        sed.setSedType("A009");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false);

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
    public void mapA009_aapenPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA009();

        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false);

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
    public void mapA009_medErendringsvedtak_forventAtSpesifikkRegelOverskriver() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA009();

        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");
        ((MedlemskapA009) sed.getMedlemskap()).getVedtak().setErendringsvedtak("ja");
        MelosysEessiMelding melding = mapper.map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false);

        assertThat(melding).isNotNull();
        assertThat(melding.isErEndring()).isTrue();
    }

    @Test
    public void mapA009_utenErendringsvedtak_forventAtResultatFraEuxOverskriver() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA009();

        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");
        MelosysEessiMelding melding = mapper.map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), true);

        assertThat(melding).isNotNull();
        assertThat(melding.isErEndring()).isTrue();
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
        vedtak.setErendringsvedtak("nei");


        return medlemskapA009;
    }
}
