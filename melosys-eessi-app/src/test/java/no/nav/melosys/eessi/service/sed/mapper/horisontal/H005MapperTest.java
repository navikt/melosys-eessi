package no.nav.melosys.eessi.service.sed.mapper.horisontal;

import java.io.IOException;
import java.net.URISyntaxException;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class H005MapperTest {

    private H005Mapper h005Mapper = new H005Mapper();

    private SedDataDto sedData;

    @Before
    public void setup() throws IOException, URISyntaxException {
        sedData = SedDataStub.getStub();
    }

    @Test
    public void mapTilSed() throws MappingException, NotFoundException {
        SED h005 = h005Mapper.mapTilSed(sedData);

        assertThat(h005).isNotNull();
        assertThat(h005.getMedlemskap()).isNull();
        assertThat(h005.getSed()).isEqualTo(SedType.H005.name());
    }
}
