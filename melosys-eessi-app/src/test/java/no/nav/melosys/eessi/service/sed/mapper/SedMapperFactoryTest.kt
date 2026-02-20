package no.nav.melosys.eessi.service.sed.mapper

import io.getunleash.FakeUnleash
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_3
import no.nav.melosys.eessi.models.sed.Konstanter.SED_VER_CDM_4_4
import no.nav.melosys.eessi.service.sed.LandkodeMapper
import no.nav.melosys.eessi.service.sed.SedDataStub
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.A001Mapper
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.A009Mapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SedMapperFactoryTest {

    private val fakeUnleash = FakeUnleash()
    private val sedMapperFactory = SedMapperFactory(fakeUnleash)

    @BeforeEach
    fun setup() {
        fakeUnleash.resetAll()
    }

    @Test
    fun oppslagavSedA001GirKorrektMapper() {
        val sedMapper = sedMapperFactory.sedMapper(SedType.A001)
        sedMapper.shouldBeInstanceOf<A001Mapper>()
    }

    @Test
    fun oppslagavSedA009GirKorrektMapper() {
        val sedMapper = sedMapperFactory.sedMapper(SedType.A009)
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
            val sedMapper = sedMapperFactory.sedMapper(sedType)
            sedMapper.getSedType() shouldBe sedType
        }
    }

    @Test
    fun `mapTilSed - CDM 4_4 av, Kosovo konverteres til Ukjent`() {
        fakeUnleash.disable(CDM_4_4)
        val sedDataDto = SedDataStub.getStub("mock/sedA009-Kosovo.json")

        val sed = sedMapperFactory.mapTilSed(SedType.A009, sedDataDto)

        sed.finnPerson().get()
            .statsborgerskap.shouldNotBeNull().shouldHaveSize(1).single().shouldNotBeNull()
            .land shouldBe LandkodeMapper.UKJENT_LANDKODE_ISO2
    }

    @Test
    fun `mapTilSed - CDM 4_4 pa, Kosovo beholdes`() {
        fakeUnleash.enable(CDM_4_4)
        val sedDataDto = SedDataStub.getStub("mock/sedA009-Kosovo.json")

        val sed = sedMapperFactory.mapTilSed(SedType.A009, sedDataDto)

        sed.finnPerson().get()
            .statsborgerskap.shouldNotBeNull().shouldHaveSize(1).single().shouldNotBeNull()
            .land shouldBe LandkodeMapper.KOSOVO_LANDKODE_ISO2
    }

    @Test
    fun `mapTilSed - CDM 4_4 av, sedVer er CDM 4_3`() {
        fakeUnleash.disable(CDM_4_4)
        val sedDataDto = SedDataStub.getStub()

        val sed = sedMapperFactory.mapTilSed(SedType.A009, sedDataDto)

        sed.sedVer shouldBe SED_VER_CDM_4_3
        sed.sedGVer shouldBe "4"
    }

    @Test
    fun `mapTilSed - CDM 4_4 pa, sedVer er CDM 4_4`() {
        fakeUnleash.enable(CDM_4_4)
        val sedDataDto = SedDataStub.getStub()

        val sed = sedMapperFactory.mapTilSed(SedType.A009, sedDataDto)

        sed.sedVer shouldBe SED_VER_CDM_4_4
        sed.sedGVer shouldBe "4"
    }

    @Test
    fun `mapTilSed - CDM 4_4 pa, alle SED-typer far versjon 4_4`() {
        fakeUnleash.enable(CDM_4_4)
        val sedDataDto = SedDataStub.getStub()

        // Tester SED-typer som kan mappes med standard test-stub.
        // A002, A004, A005, A010 krever spesielle DTOer og testes i egne mapper-tester.
        val sedTyper = listOf(
            SedType.A003, SedType.A009, SedType.A011, SedType.A012
        )

        for (sedType in sedTyper) {
            val sed = sedMapperFactory.mapTilSed(sedType, sedDataDto)
            sed.sedVer shouldBe SED_VER_CDM_4_4
            sed.sedGVer shouldBe "4"
        }
    }
}
