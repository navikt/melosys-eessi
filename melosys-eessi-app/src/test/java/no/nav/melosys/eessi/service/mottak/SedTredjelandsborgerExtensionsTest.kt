package no.nav.melosys.eessi.service.mottak

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.argumentSet
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SedTredjelandsborgerExtensionsTest {

    fun sedErA003OgTredjelandsborgerUtenNorgeSomArbeidsstedTestData() = listOf(
        TestCaseBuilder()
            .name("Ikke A003 SED")
            .sed {
                sedType(SedType.A001)
            }
            .avsenderLand("NO")
            .expectedResult(false),


        TestCaseBuilder()
            .name("Norge er lovvalgsland")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("NO")
            }
            .avsenderLand("NO")
            .expectedResult(false),

        TestCaseBuilder()
            .name("Person har norsk fnr eller d-nr")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedNorskPin("12345678901")
            }
            .avsenderLand("NO")
            .expectedResult(false),

        TestCaseBuilder()
            .name("Person er EØS-borger, så ikke en tredjelandsborger")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("SE", "DK")
            }
            .avsenderLand("NO")
            .expectedResult(false),

        TestCaseBuilder()
            .name("Norge er nevnt som arbeidssted")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
                    .arbeidssted("NO")
            }
            .avsenderLand("NO")
            .expectedResult(false),

        TestCaseBuilder()
            .name("Norge er nevnt som arbeidsland")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
                    .arbeidsland("NO")
            }
            .avsenderLand("NO")
            .expectedResult(false)
            .reason("Norge er nevnt som arbeidssted"),

        TestCaseBuilder()
            .name("Avsender er fra godkjent land for unntak")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
            }
            .avsenderLand("CH")
            .expectedResult(false),

        TestCaseBuilder()
            .name("Tredjelandsborger uten Norge som arbeidssted")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .personMedStatsborgerskap("US")
            }
            .avsenderLand("FR")
            .expectedResult(true),

        TestCaseBuilder()
            .name("Ingen person funnet")
            .sed {
                sedType(SedType.A003)
                    .vedtakLand("SE")
                    .utenPerson()
            }
            .avsenderLand("NO")
            .expectedResult(false)
    ).map { it.build() }

    @ParameterizedTest(name = "{index} - {argumentSetName} Lovvalg:{1} return:{2}")
    @MethodSource("sedErA003OgTredjelandsborgerUtenNorgeSomArbeidsstedTestData")
    fun `sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted skal returnere riktig resultat`(
        sed: SED,
        avsenderLand: String,
        expectedResult: Boolean,
        expectedReason: String
    ) {
        val result = sed.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted({ avsenderLand }) { reason ->
            reason shouldBe expectedReason
        }

        result shouldBe expectedResult
    }
}

class SEDTestBuilder {
    private var sedType: SedType = SedType.A003
    private var vedtakLand: String = "SE"
    private var person: Person? = Person(statsborgerskap = listOf(Statsborgerskap(land = "US")))
    private var arbeidssted: List<Arbeidssted> = emptyList()
    private var arbeidsland: List<Arbeidsland> = emptyList()

    fun sedType(sedType: SedType) = apply { this.sedType = sedType }

    fun vedtakLand(land: String) = apply { this.vedtakLand = land }

    fun personMedStatsborgerskap(vararg landkoder: String) = apply {
        this.person = Person(
            statsborgerskap = landkoder.map { Statsborgerskap(land = it) }
        )
    }

    fun personMedNorskPin(personnummer: String) = apply {
        this.person = Person(
            statsborgerskap = listOf(Statsborgerskap(land = "US")),
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

class TestCaseBuilder {
    private var name: String = ""
    private var sedBuilder: SEDTestBuilder = SEDTestBuilder()
    private var avsenderLand: String = "NO"
    private var expectedResult: Boolean = false
    private var reason: String = ""

    fun name(name: String) = apply {
        this.name = name
        this.reason = name
    }

    fun sed(configure: SEDTestBuilder.() -> SEDTestBuilder) = apply {
        this.sedBuilder = configure(sedBuilder)
    }

    fun avsenderLand(land: String) = apply { this.avsenderLand = land }

    fun expectedResult(result: Boolean) = apply { this.expectedResult = result }
    fun reason(result: String) = apply { this.reason = result }

    fun build(): Arguments.ArgumentSet = argumentSet(name, sedBuilder.build(), avsenderLand, expectedResult, reason)
}
