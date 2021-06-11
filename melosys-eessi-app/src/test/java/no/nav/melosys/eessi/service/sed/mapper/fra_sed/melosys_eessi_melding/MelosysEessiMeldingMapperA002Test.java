package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;


import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Periode;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning.AVSLAG;
import static no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning.DELVIS_INNVILGELSE;
import static no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperA002Test {

    private SedHendelse sedHendelse;
    private SakInformasjon sakInformasjon;

    @BeforeEach
    public void setup() {
        sedHendelse = createSedHendelse();
        sakInformasjon = createSakInformasjon();
    }

    @Test
    public void mapA002_delvisInnvilget_verifiserDataSatt() {
        SED sed = createSed(hentMedlemskap(false));


        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapperFactory.getMapper(SedType.A002)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak()).isNotNull();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getBeslutning()).isEqualTo(
                DELVIS_INNVILGELSE);
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getBegrunnelse()).isNotEmpty();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getDelvisInnvilgetPeriode()).isNotNull();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getDelvisInnvilgetPeriode().getFom()).isEqualTo("2000-12-12");
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getDelvisInnvilgetPeriode().getTom()).isEqualTo("2000-12-12");
    }

    @Test
    public void mapA002_avslag_verifiserDataSatt() {
        SED sed = createSed(hentMedlemskap(true));

        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapperFactory.getMapper(SedType.A002)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1");

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak()).isNotNull();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getBeslutning()).isEqualTo(
                AVSLAG);
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getBegrunnelse()).isNotEmpty();
        assertThat(melosysEessiMelding.getSvarAnmodningUnntak().getDelvisInnvilgetPeriode()).isNull();
    }

    private MedlemskapA002 hentMedlemskap(boolean avslag) {
        MedlemskapA002 medlemskap = new MedlemskapA002();

        UnntakA002 unntak = new UnntakA002();

        VedtakA002 vedtak = new VedtakA002();

        if (!avslag) {
            Periode periode = new Periode();

            Fastperiode fastperiode = new Fastperiode();
            fastperiode.setStartdato("2000-12-12");
            fastperiode.setSluttdato("2000-12-12");

            periode.setFastperiode(fastperiode);
            vedtak.setAnnenperiode(periode);
        }

        vedtak.setResultat(avslag ? "ikke_godkjent" : "godkjent_for_annen_periode");
        vedtak.setBegrunnelse("tadadada fritekst");

        unntak.setVedtak(vedtak);
        medlemskap.setUnntak(unntak);

        return medlemskap;
    }

}
