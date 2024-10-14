package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.controller.dto.Adressetype
import no.nav.melosys.eessi.models.sed.SED
import org.junit.jupiter.api.Test

class SedGrunnlagMapperA001Test {

    @Test
    fun map_forventetVerdier() {
        val sedGrunnlagDto = SedGrunnlagMapperA001().map(hentSed())

        sedGrunnlagDto.shouldNotBeNull()
            .sedType shouldBe "A001"

        withClue("Utenlandsk ident har rett felt") {
            sedGrunnlagDto.utenlandskIdent.shouldNotBeNull().single().run {
                ident shouldBe "15225345345"
                landkode shouldBe "BG"
                erUtenlandsk() shouldBe true
            }
        }

        withClue("Bostedsadresse har rett felt") {
            sedGrunnlagDto.bostedsadresse.shouldNotBeNull().run {
                adressetype shouldBe Adressetype.BOSTEDSADRESSE
                land shouldBe "BE"
                gateadresse shouldBe "Testgate Testbyggnavn"
            }
        }

        withClue("Arbeidssteder har rett info") {
            sedGrunnlagDto.arbeidssteder.shouldNotBeNull().shouldHaveSize(2).apply {
                first().run {
                    navn shouldBe "Testarbeidsstednavn"
                    isFysisk shouldBe false
                    hjemmebase shouldBe "Testarbeidsstedbase"
                }
                last().run {
                    navn shouldBe "Testarbeidsstednavn2"
                    isFysisk shouldBe true
                    hjemmebase shouldBe "Testarbeidsstedbase2"
                }
            }
        }

        withClue("Arbeidssteder har rette adresser") {
            sedGrunnlagDto.arbeidssteder.shouldNotBeNull().shouldHaveSize(2).apply {
                first().run {
                    adresse.shouldNotBeNull().run {
                        land shouldBe "EE"
                        postnr shouldBe "Testarbeidsstedpostkode"
                        poststed shouldBe "Testarbeidsstedby"
                        region shouldBe "Testarbeidsstedregion"
                        gateadresse shouldBe "Testarbeidsstedgate Testarbeidsstedbygning"
                    }
                }
                last().run {
                    adresse.shouldNotBeNull().run {
                        land shouldBe "CY"
                        postnr shouldBe null
                        poststed shouldBe "Testarbeidsstedby2"
                        region shouldBe null
                        gateadresse shouldBe "Testarbeidsstedgate2 Testarbeidsstedbygning2"
                    }
                }
            }
        }
    }

    companion object {
        private fun hentSed(): SED {
            val jsonUrl = SedGrunnlagMapperA001Test::class.java.classLoader.getResource("mock/sedA001.json")
            return ObjectMapper().readValue(jsonUrl, SED::class.java)
        }
    }
}
