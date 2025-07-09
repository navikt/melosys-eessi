package no.nav.melosys.eessi.service.mottak

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.argumentSet
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SedTredjelandsborgerExtensionsTest {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SedTredjelandsborgerExtensionsTest {

        fun sedErA003OgTredjelandsborgerUtenNorgeSomArbeidsstedTestData() = listOf(
            argumentSet(
                "Ikke A003 SED",
                SEDTestBuilder()
                    .sedType(SedType.A001)
                    .build(),
                "NO",
                false
            ),
            argumentSet(
                "A003 med Norge som lovvalgsland",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("NO")
                    .build(),
                "NO",
                false
            ),
            argumentSet(
                "Person har norsk personnummer",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedNorskPin("12345678901")
                    .build(),
                "NO",
                false
            ),
            argumentSet(
                "Person er EØS-borger",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("SE", "DK")
                    .build(),
                "NO",
                false
            ),
            argumentSet(
                "Norge er nevnt som arbeidssted",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
                    .arbeidssted("NO")
                    .build(),
                "NO",
                false
            ),
            argumentSet(
                "Norge er nevnt som arbeidsland",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
                    .arbeidsland("NO")
                    .build(),
                "NO",
                false
            ),
            argumentSet(
                "Avsender er fra godkjent land for unntak",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
                    .build(),
                "CH", // Godkjent land for unntak
                false
            ),
            argumentSet(
                "Tredjelandsborger uten Norge som arbeidssted - skal returnere true",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
                    .build(),
                "FR",
                true
            ),
            argumentSet(
                "Ingen person funnet",
                SEDTestBuilder()
                    .sedType(SedType.A003)
                    .vedtakLand("SE")
                    .utenPerson()
                    .build(),
                "NO",
                false
            )
        )

        @ParameterizedTest(name = "{index} - {argumentSetName} Lovvalg:{1} return:{2}")
        @MethodSource("sedErA003OgTredjelandsborgerUtenNorgeSomArbeidsstedTestData")
        fun `sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted skal returnere riktig resultat`(
            sed: SED,
            avsenderLand: String,
            expectedResult: Boolean
        ) {
            // Act
            val result = sed.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted { avsenderLand }

            // Assert
            result shouldBe expectedResult
        }
    }
}

class SEDTestBuilder {
    private var sedType: SedType = SedType.A003
    private var vedtakLand: String = "SE"
    private var person: Person? = Person(
        statsborgerskap = listOf(Statsborgerskap(land = "US")),
        foedselsdato = "1990-01-01"
    )
    private var arbeidssted: List<Arbeidssted> = emptyList()
    private var arbeidsland: List<Arbeidsland> = emptyList()

    fun sedType(sedType: SedType) = apply { this.sedType = sedType }

    fun vedtakLand(land: String) = apply { this.vedtakLand = land }

    fun personMedStatsborgerskap(vararg landkoder: String) = apply {
        this.person = Person(
            statsborgerskap = landkoder.map { Statsborgerskap(land = it) },
            foedselsdato = "1990-01-01"
        )
    }

    fun personMedNorskPin(personnummer: String) = apply {
        this.person = Person(
            statsborgerskap = listOf(Statsborgerskap(land = "US")),
            foedselsdato = "1990-01-01",
            pin = setOf(Pin(land = "NO", identifikator = personnummer))
        )
    }

    fun arbeidssted(land: String) = apply {
        this.arbeidssted = listOf(
            Arbeidssted(
                adresse = Adresse(land = land)
            )
        )
    }

    fun arbeidsland(land: String) = apply {
        this.arbeidsland = listOf(
            Arbeidsland(land = land)
        )
    }

    fun utenPerson() = apply { this.person = null }

    fun build(): SED {
        val medlemskap = when (sedType) {
            SedType.A003 -> MedlemskapA003(
                vedtak = VedtakA003(land = vedtakLand)
            )
            else -> null
        }

        return SED(
            sedType = sedType.name,
            medlemskap = medlemskap,
            nav = Nav(
                bruker = Bruker(person = person),
                arbeidssted = arbeidssted.takeIf { it.isNotEmpty() },
                arbeidsland = arbeidsland.takeIf { it.isNotEmpty() }
            )
        )
    }
}

