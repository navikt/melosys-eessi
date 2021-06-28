package no.nav.melosys.eessi.service.sed.mapper.til_sed.horisontal;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HorisontalSedMapperTest {

    private final HorisontalSedMapper h005Mapper = new HorisontalSedMapper(SedType.H005);

    private SedDataDto sedData;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
    }

    @Test
    void mapTilSed() throws MappingException, NotFoundException {
        SED h005 = h005Mapper.mapTilSed(sedData);

        assertThat(h005).isNotNull();
        assertThat(h005.getMedlemskap()).isNull();
        assertThat(h005.getSedType()).isEqualTo(SedType.H005.name());
    }
}
