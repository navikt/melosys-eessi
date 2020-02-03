package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.A001Mapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.A009Mapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SedMapperFactoryTest {

    @Test
    public void oppslagavSedA001GirKorrektMapper() throws Exception {
        SedMapper sedMapper = SedMapperFactory.sedMapper(SedType.A001);
        assertThat(sedMapper).isInstanceOf(A001Mapper.class);
    }

    @Test
    public void oppslagavSedA009GirKorrektMapper() throws Exception {
        SedMapper sedMapper = SedMapperFactory.sedMapper(SedType.A009);
        assertThat(sedMapper).isInstanceOf(A009Mapper.class);
    }
}
