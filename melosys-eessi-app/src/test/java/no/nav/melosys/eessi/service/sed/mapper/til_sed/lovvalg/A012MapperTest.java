package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA012;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class A012MapperTest {
    private final A012Mapper a012Mapper = new A012Mapper();

    @Test
    void mapTilSed_forventSed() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedData = SedDataStub.getStub();
        SED a012 = a012Mapper.mapTilSed(sedData, false);

        assertThat(a012).isNotNull();
        assertThat(a012.getMedlemskap()).isInstanceOf(MedlemskapA012.class);
    }
}
