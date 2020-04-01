package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import java.util.Collections;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA001;
import no.nav.melosys.eessi.models.sed.nav.Fastperiode;
import no.nav.melosys.eessi.models.sed.nav.Grunnlag;
import no.nav.melosys.eessi.models.sed.nav.Land;
import no.nav.melosys.eessi.models.sed.nav.Unntak;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.junit.Test;

import static no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperStubs.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperA001Test {

    @Test
    public void mapA001_forventRettFelt() {
        SED sed = createSed(hentMedlemskap());
        SedHendelse sedHendelse = createSedHendelse();
        SakInformasjon sakInformasjon = createSakInformasjon();
        MelosysEessiMelding melosysEessiMelding = MelosysEessiMeldingMapperFactory.getMapper(SedType.A001)
                .map("123",
                        sed,
                        sedHendelse.getRinaDokumentId(),
                        sedHendelse.getRinaSakId(),
                        sedHendelse.getSedType(),
                        sedHendelse.getBucType(),
                        sakInformasjon.getJournalpostId(),
                        sakInformasjon.getDokumentId(),
                        sakInformasjon.getGsakSaksnummer(),
                        false
                );

        assertThat(melosysEessiMelding).isNotNull();
        assertThat(melosysEessiMelding.getArtikkel()).isEqualTo("16_1");
        assertThat(melosysEessiMelding.getLovvalgsland()).isEqualTo("NO");
        assertThat(melosysEessiMelding.getAnmodningUnntak()).isNotNull();
        assertThat(melosysEessiMelding.getAnmodningUnntak().getUnntakFraLovvalgsbestemmelse()).isEqualTo("12_1");
        assertThat(melosysEessiMelding.getAnmodningUnntak().getUnntakFraLovvalgsland()).isEqualTo("SE");
    }

    private MedlemskapA001 hentMedlemskap() {
        MedlemskapA001 medlemskap = new MedlemskapA001();

        Fastperiode fastperiode = new Fastperiode();
        fastperiode.setSluttdato("2019-12-01");
        fastperiode.setStartdato("2019-05-01");
        medlemskap.setSoeknadsperiode(fastperiode);

        Land sverige = new Land();
        sverige.setLandkode("SE");
        medlemskap.setNaavaerendemedlemskap(Collections.singletonList(sverige));

        Land norge = new Land();
        norge.setLandkode("NO");
        medlemskap.setForespurtmedlemskap(Collections.singletonList(norge));

        Unntak unntak = new Unntak();
        Grunnlag grunnlag = new Grunnlag();
        grunnlag.setArtikkel("12_1");
        unntak.setGrunnlag(grunnlag);
        medlemskap.setUnntak(unntak);

        return medlemskap;
    }
}
