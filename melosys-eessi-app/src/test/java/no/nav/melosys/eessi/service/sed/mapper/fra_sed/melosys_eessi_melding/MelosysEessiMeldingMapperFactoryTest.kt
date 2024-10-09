package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.models.SedType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MelosysEessiMeldingMapperFactoryTest {
    private val melosysEessiMeldingMapperFactory = MelosysEessiMeldingMapperFactory("dummy")

    @Test
    fun hentA002Mapper() {
        val mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.A002)
        Assertions.assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA002::class.java)
    }

    @Test
    fun hentA003Mapper() {
        val mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.A003)
        Assertions.assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA003::class.java)
    }

    @Test
    fun hentA009Mapper() {
        val mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.A009)
        Assertions.assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA009::class.java)
    }

    @Test
    fun hentA010Mapper() {
        val mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.A010)
        Assertions.assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA010::class.java)
    }

    @Test
    fun hentA011Mapper() {
        val mapper = melosysEessiMeldingMapperFactory.getMapper(SedType.A011)
        Assertions.assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA011::class.java)
    }
}
