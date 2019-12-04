package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.A001Mapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.A009Mapper;
import no.nav.melosys.eessi.service.sed.mapper.lovvalg.LovvalgSedMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class SedMapperFactoryTest {

    public class IkkeInstansierbarSedMapper implements LovvalgSedMapper {

        private IkkeInstansierbarSedMapper() {
        }

        @Override
        public Medlemskap getMedlemskap(SedDataDto sedData) {
            return null;
        }

        @Override
        public SedType getSedType() {
            return null;
        }
    }

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

    @Test
    public void oppslagAvIkkeInstansierbarSedMapperKasterUnntak() {
        SedMapperFactory.sedMappers.put(SedType.A012, IkkeInstansierbarSedMapper.class);
        Throwable unntak = catchThrowable(() -> SedMapperFactory.sedMapper(SedType.A012));
        assertThat(unntak).isInstanceOf(MappingException.class).hasCauseInstanceOf(NoSuchMethodException.class);
    }
}
