package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class A002MapperTest {
    private final A002Mapper a002Mapper = new A002Mapper();

    @Test
    void mapTilSed_forventSed() throws IOException, URISyntaxException {
        SedDataDto sedData = SedDataStub.getStub();
        SvarAnmodningUnntakDto svarAnmodningUnntakDto = new SvarAnmodningUnntakDto(
                SvarAnmodningUnntakBeslutning.AVSLAG,
                "begrunnelse",
                new Periode(LocalDate.now(), LocalDate.now().plusDays(1L))
        );
        sedData.setSvarAnmodningUnntak(svarAnmodningUnntakDto);

        SED a002 = a002Mapper.mapTilSed(sedData);
        assertThat(a002).isNotNull();
        assertThat(a002.getMedlemskap()).isInstanceOf(MedlemskapA002.class);
    }

    @Test
    void mapTilSed_utenSvarAnmodningUnntak_forventException() throws IOException, URISyntaxException {
        SedDataDto sedData = SedDataStub.getStub();
        assertThatExceptionOfType(MappingException.class)
                .isThrownBy(() -> a002Mapper.mapTilSed(sedData))
                .withMessageContaining("Trenger SvarAnmodningUnntak");
    }
}
