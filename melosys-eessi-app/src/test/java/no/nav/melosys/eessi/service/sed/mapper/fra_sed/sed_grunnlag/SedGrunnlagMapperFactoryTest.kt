package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag.SedGrunnlagMapperA003
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag.SedGrunnlagMapperFactory.Companion.getMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class SedGrunnlagMapperFactoryTest {
    @get:Test
    val mapper_returnsCorrectMapper_A001: Unit
        get() {
            Assertions.assertThat(getMapper(SedType.A001))
                .isInstanceOf(SedGrunnlagMapperA001::class.java)
        }

    @get:Test
    val mapper_returnsCorrectMapper_A003: Unit
        get() {
            Assertions.assertThat(getMapper(SedType.A003))
                .isInstanceOf(SedGrunnlagMapperA003::class.java)
        }

    @get:Test
    val mapper_returnsMappingExceptionOnNonExistingMapper: Unit
        get() {
            Assertions.assertThatExceptionOfType(MappingException::class.java)
                .isThrownBy { getMapper(SedType.A005) }
                .withMessage("Sed-type A005 støttes ikke")
        }
}
