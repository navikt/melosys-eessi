package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import org.junit.jupiter.api.Test

class SedGrunnlagMapperFactoryTest {

    @Test
    fun getMapper_returnsCorrectMapper_A001() {
        SedGrunnlagMapperFactory.getMapper(SedType.A001)
            .shouldBeInstanceOf<SedGrunnlagMapperA001>()
    }

    @Test
    fun getMapper_returnsCorrectMapper_A003() {
        SedGrunnlagMapperFactory.getMapper(SedType.A003)
            .shouldBeInstanceOf<SedGrunnlagMapperA003>()
    }

    @Test
    fun getMapper_returnsMappingExceptionOnNonExistingMapper() {
        shouldThrow<MappingException> {
            SedGrunnlagMapperFactory.getMapper(SedType.A005)
        }.message shouldBe "Sed-type A005 støttes ikke"
    }
}
