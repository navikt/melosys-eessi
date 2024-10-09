package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.models.sed.SED
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.function.Function

internal class SedGrunnlagMapperA001Test {
    @Test
    @Throws(IOException::class)
    fun map_forventetVerdier() {
        val sedGrunnlagDto = SedGrunnlagMapperA001().map(hentSed())

        Assertions.assertThat(sedGrunnlagDto).isNotNull()
        Assertions.assertThat(sedGrunnlagDto.sedType).isEqualTo("A001")

        Assertions.assertThat(sedGrunnlagDto.utenlandskIdent)
            .`as`("Utenlandsk ident har rett felt")
            .extracting(
                Function<Ident, Any> { obj: Ident -> obj.ident },
                Function<Ident, Any> { obj: Ident -> obj.landkode },
                Function<Ident, Any> { obj: Ident -> obj.erUtenlandsk() })
            .containsExactlyInAnyOrder(Assertions.tuple("15225345345", "BG", true))

        Assertions.assertThat(sedGrunnlagDto.bostedsadresse)
            .`as`("Bostedsadresse har rett felt")
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.adressetype },
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(Adressetype.BOSTEDSADRESSE, "BE", "Testgate Testbyggnavn")

        Assertions.assertThat(sedGrunnlagDto.arbeidssteder)
            .`as`("Arbeidssteder har rett info")
            .extracting(
                Function<Arbeidssted, Any> { obj: Arbeidssted -> obj.navn },
                Function<Arbeidssted, Any> { obj: Arbeidssted -> obj.isFysisk },
                Function<Arbeidssted, Any> { obj: Arbeidssted -> obj.hjemmebase })
            .containsExactlyInAnyOrder(
                Assertions.tuple("Testarbeidsstednavn", false, "Testarbeidsstedbase"),
                Assertions.tuple("Testarbeidsstednavn2", true, "Testarbeidsstedbase2")
            )

        Assertions.assertThat(sedGrunnlagDto.arbeidssteder)
            .`as`("Arbeidssteder har rette adresser")
            .extracting<Adresse, RuntimeException> { obj: Arbeidssted -> obj.adresse }
            .extracting(
                Function<Adresse, Any> { obj: Adresse -> obj.land },
                Function<Adresse, Any> { obj: Adresse -> obj.postnr },
                Function<Adresse, Any> { obj: Adresse -> obj.poststed },
                Function<Adresse, Any> { obj: Adresse -> obj.region },
                Function<Adresse, Any> { obj: Adresse -> obj.gateadresse })
            .containsExactlyInAnyOrder(
                Assertions.tuple(
                    "EE",
                    "Testarbeidsstedpostkode",
                    "Testarbeidsstedby",
                    "Testarbeidsstedregion",
                    "Testarbeidsstedgate Testarbeidsstedbygning"
                ),
                Assertions.tuple("CY", null, "Testarbeidsstedby2", null, "Testarbeidsstedgate2 Testarbeidsstedbygning2")
            )
    }

    companion object {
        @Throws(IOException::class)
        private fun hentSed(): SED {
            val jsonUrl = SedGrunnlagMapperA001Test::class.java.classLoader.getResource("mock/sedA001.json")
            return ObjectMapper().readValue(jsonUrl, SED::class.java)
        }
    }
}
