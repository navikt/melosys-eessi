package no.nav.melosys.eessi.service.mottak

import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.service.mottak.SedA003UnntaksreglerForTredjelandsborgere.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.argumentSet
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SedTredjelandsborgerExtensionsTest {

    fun sedErA003OgTredjelandsborgerUtenNorgeSomArbeidsstedTestData() = listOf(
        sedTestCase {
            name("Ikke A003 SED")
            sed {
                sedType(SedType.A001)
            }
            avsenderLand("NO")
            expectedResult(false)
        },
        sedTestCase {
            name("Norge er lovvalgsland")
            sed {
                sedType(SedType.A003)
                vedtakLand("NO")
            }
            avsenderLand("NO")
            expectedResult(false)
        },
        sedTestCase {
            name("Person har norsk fnr eller d-nr")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedNorskPin("30056928150")
            }
            avsenderLand("NO")
            expectedResult(false)
        },
        sedTestCase {
            name("Person er EØS-borger, så ikke en tredjelandsborger")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedStatsborgerskap("SE", "DK")
            }
            avsenderLand("NO")
            expectedResult(false)
        },
        sedTestCase {
            name("Norge er nevnt i arbeidssted.address.land")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedStatsborgerskap("US")
                arbeidssted(
                    Arbeidssted(
                        adresse = Adresse(land = "NO")
                    )
                )
            }
            avsenderLand("NO")
            expectedResult(false)
            reason("Norge er nevnt som arbeidssted")
        },
        sedTestCase {
            name("Norge er nevnt som i arbeidsland.land")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedStatsborgerskap("US")
                arbeidsland(
                    Arbeidsland(land = "NO")
                )
            }
            avsenderLand("NO")
            expectedResult(false)
            reason("Norge er nevnt som arbeidssted")
        },
        sedTestCase {
            name("Norge er nevnt som land i arbeidsland.arbeidssted.address.land")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedStatsborgerskap("US")
                arbeidsland(
                    Arbeidsland(
                        arbeidssted = listOf(Arbeidssted(adresse = Adresse(land = "NO")))
                    )
                )
            }
            avsenderLand("NO")
            expectedResult(false)
            reason("Norge er nevnt som arbeidssted")
        },
        sedTestCase {
            name("Avsender er fra godkjent land for unntak")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedStatsborgerskap("US")
            }
            avsenderLand("CH")
            expectedResult(false)
        },
        sedTestCase {
            name("Tredjelandsborger uten Norge som arbeidssted")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                personMedStatsborgerskap("US")
            }
            avsenderLand("FR")
            expectedResult(true)
        },
        sedTestCase {
            name("Ingen person funnet")
            sed {
                sedType(SedType.A003)
                vedtakLand("SE")
                utenPerson()
            }
            avsenderLand("NO")
            expectedResult(false)
        }
    )

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
            pin = listOf(Pin(land = "NO", identifikator = personnummer))
        )
    }

    fun arbeidssted(arbeidssted: Arbeidssted) = apply {
        this.arbeidssted = listOf(arbeidssted)
    }

    fun arbeidsland(arbeidsland: Arbeidsland) = apply {
        this.arbeidsland = listOf(arbeidsland)
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

fun sedTestCase(configure: TestCaseBuilder.() -> Unit) =
    TestCaseBuilder().apply(configure).build()
