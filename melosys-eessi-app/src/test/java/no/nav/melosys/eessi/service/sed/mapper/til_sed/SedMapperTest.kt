package no.nav.melosys.eessi.service.sed.mapper.til_sed

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.controller.dto.Adressetype
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
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

    @Test
    fun `map tilleggsnavn til bygning for alle steder adresse blir brukt`() {
        val sed: SED = sedMapper.mapTilSed(SedDataStub.getStub {
            this.bostedsadresse!!.tilleggsnavn = "bostedsadresse Tilleggsnavn"
            this.kontaktadresse!!.tilleggsnavn = "kontaktadresse Tilleggsnavn"
            this.oppholdsadresse!!.tilleggsnavn = "oppholdsadresse Tilleggsnavn"
            arbeidsland.forEach {
                it.arbeidssted.forEach { arbeidssted ->
                    arbeidssted.adresse.tilleggsnavn = "Arbeidsland Tilleggsnavn"
                }
            }
        })
        sed.nav.shouldNotBeNull().run {
            bruker.shouldNotBeNull()
                .adresse.shouldNotBeNull().shouldHaveSize(3)
                .map { it.bygning }
                .shouldContainExactly(
                    "bostedsadresse Tilleggsnavn",
                    "kontaktadresse Tilleggsnavn",
                    "oppholdsadresse Tilleggsnavn"
                )

            arbeidsland.shouldNotBeNull().single()
                .arbeidssted.single()
                .adresse.shouldNotBeNull()
                .bygning shouldBe "Arbeidsland Tilleggsnavn"
        }
    }

    @Test
    fun `tilleggsnavn til bygning skal bli en gyldig EESSIMediumString`() {
        val sed: SED = sedMapper.mapTilSed(SedDataStub.getStub {
            this.bostedsadresse!!.tilleggsnavn = " "
            this.kontaktadresse!!.tilleggsnavn = (1..200).joinToString("") { "*" }
            this.oppholdsadresse!!.tilleggsnavn = "oppholdsadresse Tilleggsnavn"
            arbeidsland.forEach {
                it.arbeidssted.forEach { arbeidssted ->
                    arbeidssted.adresse.tilleggsnavn = " "
                }
            }
        })
        sed.nav.shouldNotBeNull().run {
            bruker.shouldNotBeNull()
                .adresse.shouldNotBeNull().shouldHaveSize(3)
                .map { it.bygning }
                .shouldContainExactly(
                    null,
                    (1..155).joinToString("") { "*" },
                    "oppholdsadresse Tilleggsnavn"
                )

            arbeidsland.shouldNotBeNull().single()
                .arbeidssted.single()
                .adresse.shouldNotBeNull()
                .bygning shouldBe null
        }
    }
}
