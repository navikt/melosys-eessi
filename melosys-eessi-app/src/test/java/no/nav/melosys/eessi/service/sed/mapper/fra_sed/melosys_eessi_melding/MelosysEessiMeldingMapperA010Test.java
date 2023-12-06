package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;


import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010;
import no.nav.melosys.eessi.models.sed.nav.AapenPeriode;
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.VedtakA010;
import no.nav.melosys.eessi.service.journalfoering.SakInformasjon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperA010Test {

    private SedHendelse sedHendelse;
    private SakInformasjon sakInformasjon;

    @BeforeEach
    public void setup() {
        sedHendelse = createSedHendelse();
        sakInformasjon = createSakInformasjon();
    }

    @Test
    public void mapA010_fastPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA010();

        SED sed = createSed(hentMedlemskap(true));
        sed.setSedType("A010");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");

        assertThat(melding).isNotNull();
        assertThat(melding.getGsakSaksnummer()).isNotNull();
        assertThat(melding.getArtikkel()).isEqualTo("11_4");
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
    public void mapA010_aapenPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA010();

        SED sed = createSed(hentMedlemskap(false));
        sed.setSedType("A009");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");

        assertThat(melding).isNotNull();
        assertThat(melding.getGsakSaksnummer()).isNotNull();
        assertThat(melding.getArtikkel()).isEqualTo("11_4");
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
    public void mapA010_medErOpprinneligvedtak_forventAtSpesifikkRegelOverskriver() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA010();

        SED sed = createSed(hentMedlemskap(true));
        sed.setSedType("A010");
        ((MedlemskapA010) sed.getMedlemskap()).getVedtak().setEropprinneligvedtak("nei");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");

        assertThat(melding).isNotNull();
        assertThat(melding.isErEndring()).isTrue();
    }

    @Test
    public void mapA010_utenErOpprinneligvedtak_forventAtResultatFraEuxOverskriver() {
        MelosysEessiMeldingMapper mapper = new MelosysEessiMeldingMapperA010();

        SED sed = createSed(hentMedlemskap(true));
        sed.setSedType("A010");
        MelosysEessiMelding melding = mapper.map("aktørid", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), true, "1");

        assertThat(melding).isNotNull();
        assertThat(melding.isErEndring()).isTrue();
    }

    private MedlemskapA010 hentMedlemskap(boolean fastperiode) {
        MedlemskapA010 medlemskapA010 = new MedlemskapA010();

        VedtakA010 vedtak = new VedtakA010();
        medlemskapA010.setVedtak(vedtak);

        PeriodeA010 periode = new PeriodeA010();
        if (fastperiode) {
            periode.setSluttdato("2019-12-01");
            periode.setStartdato("2019-05-01");
        } else {
            periode.setAapenperiode(new AapenPeriode());
            periode.getAapenperiode().setStartdato("2019-05-01");
        }
        vedtak.setGjelderperiode(periode);

        vedtak.setLand("SE");
        vedtak.setEropprinneligvedtak("ja");

        medlemskapA010.setMeldingomlovvalg(new MeldingOmLovvalg());
        medlemskapA010.getMeldingomlovvalg().setArtikkel("11_4");

        return medlemskapA010;
    }
}
