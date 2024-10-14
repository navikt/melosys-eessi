package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SedGrunnlagMapperFactoryTest {

    @Test
    void getMapper_returnsCorrectMapper_A001() {
        assertThat(SedGrunnlagMapperFactory.getMapper(SedType.A001))
            .isInstanceOf(SedGrunnlagMapperA001.class);
    }

    @Test
    void getMapper_returnsCorrectMapper_A003() {
        assertThat(SedGrunnlagMapperFactory.getMapper(SedType.A003))
            .isInstanceOf(SedGrunnlagMapperA003.class);
    }

    @Test
    void getMapper_returnsMappingExceptionOnNonExistingMapper() {
        assertThatExceptionOfType(MappingException.class)
            .isThrownBy(() -> SedGrunnlagMapperFactory.getMapper(SedType.A005))
            .withMessage("Sed-type A005 støttes ikke");
    }
}
