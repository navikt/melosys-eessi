package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010;
import no.nav.melosys.eessi.models.sed.nav.VedtakA003;
import org.junit.Test;
import static no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;


public class MelosysEessiMeldingMapperA003Test {

    @Test
    public void mapA003_verifiserDataSatt() {
        SED sed = createSed(hentMedlemskap());
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapperFactory
                .getMapper(SedType.A003).map("123", sed, createSedHendelse(), createSakInformasjon(), false);

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
