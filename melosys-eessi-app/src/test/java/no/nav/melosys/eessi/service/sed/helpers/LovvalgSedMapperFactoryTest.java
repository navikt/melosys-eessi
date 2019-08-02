package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap;
import no.nav.melosys.eessi.service.sed.mapper.A009Mapper;
import no.nav.melosys.eessi.service.sed.mapper.LovvalgSedMapper;
import no.nav.melosys.eessi.service.sed.mapper.SedMapper;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class LovvalgSedMapperFactoryTest {

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
    public void oppslagavSedGirKorrektMapper() throws Exception {
        SedMapper sedMapper = LovvalgSedMapperFactory.sedMapper(SedType.A009);
        assertThat(sedMapper).isInstanceOf(A009Mapper.class);
    }

    @Test
    public void oppslagAvIkkeInstansierbarSedMapperKasterUnntak() {
        LovvalgSedMapperFactory.sedMappers.put(SedType.A012, IkkeInstansierbarSedMapper.class);
        Throwable unntak = catchThrowable(() -> LovvalgSedMapperFactory.sedMapper(SedType.A012));
        assertThat(unntak).isInstanceOf(MappingException.class).hasCauseInstanceOf(InstantiationException.class);
    }
}