package no.nav.melosys.eessi.service.sed.helpers

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.service.sed.helpers.SedMapperFactory.sedMapper
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.A001Mapper
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.A009Mapper
import org.junit.jupiter.api.Test

class SedMapperFactoryTest {

    @Test
    fun oppslagavSedA001GirKorrektMapper() {
        val sedMapper = sedMapper(SedType.A001)
        sedMapper.shouldBeInstanceOf<A001Mapper>()
    }

    @Test
    fun oppslagavSedA009GirKorrektMapper() {
        val sedMapper = sedMapper(SedType.A009)
        sedMapper.shouldBeInstanceOf<A009Mapper>()
    }

    @Test
    fun oppslagAvAlleSeder_girKorrektMapper() {
        val sedTyperMedMapper = listOf(
            SedType.A001,
            SedType.A002,
            SedType.A003,
            SedType.A004,
            SedType.A005,
            SedType.A008,
            SedType.A009,
            SedType.A010,
            SedType.A011,
            SedType.A012,

            SedType.H001,
            SedType.H003,
            SedType.H004,
            SedType.H005,
            SedType.H010,
            SedType.H011,
            SedType.H020,
            SedType.H021,
            SedType.H061,
            SedType.H062,
            SedType.H065,
            SedType.H066,
            SedType.H070,
            SedType.H120,
            SedType.H121,
            SedType.H130
        )

        for (sedType in sedTyperMedMapper) {
            val sedMapper = sedMapper(sedType)
            sedMapper.getSedType() shouldBe sedType
        }
    }
}
