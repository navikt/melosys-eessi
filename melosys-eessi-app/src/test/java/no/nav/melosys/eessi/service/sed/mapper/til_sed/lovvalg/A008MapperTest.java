package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class A008MapperTest {

    private final A008Mapper a008Mapper = new A008Mapper();

    private SedDataDto sedData;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
    }

    @Test
    void mapTilSed() throws MappingException, NotFoundException {
        SED sed = a008Mapper.mapTilSed(sedData);
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA008.class);

        MedlemskapA008 medlemskap = (MedlemskapA008) sed.getMedlemskap();
        assertThat(medlemskap).isNotNull();
    }

}
