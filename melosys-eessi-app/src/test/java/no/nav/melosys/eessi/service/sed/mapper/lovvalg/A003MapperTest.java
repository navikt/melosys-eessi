package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class A003MapperTest {

    private final LovvalgSedMapper sedMapper = new A003Mapper();

    @Test
    public void mapTilSed() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedDataDto = SedDataStub.getStub();
        SED sed = sedMapper.mapTilSed(sedDataDto);

        assertThat(sed).isNotNull();
        assertThat(sed.getMedlemskap()).isInstanceOf(MedlemskapA003.class);
    }
}
