package no.nav.melosys.eessi.service.sed.mapper.til_sed

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import no.nav.melosys.eessi.controller.dto.Adressetype
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.Test

class SedMapperTest {
    private val sedMapper: SedMapper = object : SedMapper {
        override fun getSedType(): SedType = SedType.A003
    }

    private val sedData: SedDataDto = SedDataStub.getStub()

    @Test
    fun hentAdresser() {
        val adresser = sedMapper.hentAdresser(sedData)

        adresser.shouldHaveSize(3).map { it.type }.shouldContainExactly(
            Adressetype.BOSTEDSADRESSE.adressetypeRina,
            Adressetype.KONTAKTADRESSE.adressetypeRina,
            Adressetype.POSTADRESSE.adressetypeRina
        )
    }

    @Test
    fun hentArbeidsland() {
        val arbeidsland = sedMapper.hentArbeidsland(sedData)

        arbeidsland.shouldHaveSize(1)
            .single().arbeidssted.shouldHaveSize(1)
    }

    @Test
    fun hentStatsborgerskap() {
        val statsborgerskap = sedMapper.hentStatsborgerskap(sedData)

        statsborgerskap.shouldHaveSize(2).shouldContainExactly(
            Statsborgerskap("NO"),
            Statsborgerskap("SE")
        )
    }
}
