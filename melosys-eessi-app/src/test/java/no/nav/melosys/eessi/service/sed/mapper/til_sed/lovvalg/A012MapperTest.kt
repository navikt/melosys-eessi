package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA012
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test

class A012MapperTest {
    @Test
    fun `map til SED forvent korrekt SED`() {
        val a012 = SedDataStub.mapTilSed<A012Mapper>(erCDM4_3 = true, testData = "mock/sedDataDtoStub.json")

        a012.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA012>()
        }
    }
}
