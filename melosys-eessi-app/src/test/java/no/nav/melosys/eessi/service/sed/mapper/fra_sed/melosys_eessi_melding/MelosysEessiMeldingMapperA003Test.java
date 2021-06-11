package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.VedtakA003;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;


public class MelosysEessiMeldingMapperA003Test {

    private SedHendelse sedHendelse;
    private SakInformasjon sakInformasjon;
    private MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory = new MelosysEessiMeldingMapperFactory("dummy");

    @BeforeEach
    public void setup() {
        sedHendelse = createSedHendelse();
        sakInformasjon = createSakInformasjon();
    }

    @Test
    public void mapA003_verifiserDataSatt() {
        SED sed = createSed(hentMedlemskap());
        MelosysEessiMelding melosysEessiMelding = melosysEessiMeldingMapperFactory.getMapper(SedType.A003)
                .map("123", sed, sedHendelse.getRinaDokumentId(), sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(), sedHendelse.getBucType(), sedHendelse.getAvsenderId(), "landkode", sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(), sakInformasjon.getGsakSaksnummer(), false, "1"
                );

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.getPeriode().getFom()).isEqualTo("2000-12-12");
        assertThat(melosysEessiMelding.isErEndring()).isTrue();
        assertThat(melosysEessiMelding.getArtikkel()).isEqualTo("13_1_b_i");
    }

    private MedlemskapA003 hentMedlemskap() {
        MedlemskapA003 medlemskap = new MedlemskapA003();
        medlemskap.setVedtak(new VedtakA003());
        medlemskap.getVedtak().setErendringsvedtak("ja");

        PeriodeA010 periodeA010 = new PeriodeA010();
        periodeA010.setStartdato("2000-12-12");
        periodeA010.setSluttdato("2000-12-12");
        medlemskap.getVedtak().setGjelderperiode(periodeA010);
        medlemskap.setRelevantartikkelfor8832004eller9872009("13_1_b_i");

        return medlemskap;
    }
}
