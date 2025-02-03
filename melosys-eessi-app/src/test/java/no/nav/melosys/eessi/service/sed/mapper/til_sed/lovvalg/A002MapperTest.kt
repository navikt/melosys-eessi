package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.controller.dto.Periode
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test
import java.time.LocalDate

class A002MapperTest {

    @Test
    fun `map til SED med version 3`() {
        val sed = SedDataStub.mapTilSed<A002Mapper>(testData = "mock/sedDataDtoStub.json") {
            svarAnmodningUnntak = SvarAnmodningUnntakDto(
                SvarAnmodningUnntakBeslutning.AVSLAG,
                "begrunnelse",
                Periode(LocalDate.now(), LocalDate.now().plusDays(1))
            )
        }

        sed.shouldNotBeNull().run {
            medlemskap.shouldBeInstanceOf<MedlemskapA002>()
            nav.shouldNotBeNull().run {
                arbeidsland.shouldNotBeNull().size shouldBe 1
            }
            sedVer shouldBe "3"
            sedGVer shouldBe "4"
        }
    }

    @Test
    fun `map til SED uten SvarAnmodningUnntak forvent Exception`() {
        val exception = shouldThrow<MappingException> {
            SedDataStub.mapTilSed<A002Mapper>(testData = "mock/sedDataDtoStub.json")
        }
        exception.message.shouldNotBeNull().shouldContain("Trenger SvarAnmodningUnntak")
    }
}
