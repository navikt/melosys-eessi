package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.VedtakDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class A003MapperTest {

    private final LovvalgSedMapper<MedlemskapA003> sedMapper = new A003Mapper();
    private SedDataDto sedData;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
        sedData.getLovvalgsperioder().get(0).setLovvalgsland("NO");

    }

    @Test
    void mapTilSed() throws MappingException, NotFoundException {
        SED sed = sedMapper.mapTilSed(sedData);

        assertThat(sed).isNotNull();
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA003.class);

        MedlemskapA003 medlemskapA003 = (MedlemskapA003) sed.getMedlemskap();
        assertThat(sed.getNav().getArbeidsgiver()).allMatch(a -> "NO".equals(a.getAdresse().getLand()));
        assertThat(medlemskapA003.getAndreland().getArbeidsgiver()).noneMatch(a -> "NO".equals(a.getAdresse().getLand()));
    }

    @Test
    void erIkkeOpprinneligVedtak_ErOpprinneligVedtakOgErEndringsvedtakSattKorrektOgDatoForrigeVedtakIkkeNull() {
        VedtakDto vedtakDto = new VedtakDto();
        vedtakDto.setErFørstegangsvedtak(false);
        vedtakDto.setDatoForrigeVedtak(LocalDate.now());
        sedData.setVedtakDto(vedtakDto);


        SED sed = sedMapper.mapTilSed(sedData);

        assertThat(sed.getMedlemskap().getClass()).isEqualTo(MedlemskapA003.class);
        MedlemskapA003 medlemskapA003 = (MedlemskapA003) sed.getMedlemskap();
        assertThat(medlemskapA003).isNotNull();
        assertThat(medlemskapA003.getVedtak().getEropprinneligvedtak()).isNull();
        assertThat(medlemskapA003.getVedtak().getErendringsvedtak()).isEqualTo("nei");
        assertThat(medlemskapA003.getVedtak().getDatoforrigevedtak()).isNotNull();
        assertThat(medlemskapA003.getVedtak().getDatoforrigevedtak()).isEqualTo(LocalDate.now().toString());
    }

    @Test
    void erOpprinneligVedtak_ErOpprinneligVedtakOgErEndringsvedtakSattKorrektOgDatoForrigeVedtakNull() {
        VedtakDto vedtakDto = new VedtakDto();
        vedtakDto.setErFørstegangsvedtak(true);
        sedData.setVedtakDto(vedtakDto);


        SED sed = sedMapper.mapTilSed(sedData);

        assertThat(sed.getMedlemskap().getClass()).isEqualTo(MedlemskapA003.class);
        MedlemskapA003 medlemskapA003 = (MedlemskapA003) sed.getMedlemskap();
        assertThat(medlemskapA003).isNotNull();
        assertThat(medlemskapA003.getVedtak().getEropprinneligvedtak()).isEqualTo("ja");
        assertThat(medlemskapA003.getVedtak().getErendringsvedtak()).isNull();
        assertThat(medlemskapA003.getVedtak().getDatoforrigevedtak()).isNull();
    }
}
