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
            name = "Ikke A003 SED"
            sed {
                type = SedType.A001
            }
            avsenderLand = "NO"
            expectedResult = false
        },
        sedTestCase {
            name = "Norge er lovvalgsland"
            sed {
                type = SedType.A003
                vedtakLand = "NO"
            }
            avsenderLand = "NO"
            expectedResult = false
        },
        sedTestCase {
            name = "Person har norsk fnr eller d-nr"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                norskPin("30056928150")
            }
            avsenderLand = "NO"
            expectedResult = false
        },
        sedTestCase {
            name = "Person er EØS-borger, så ikke en tredjelandsborger"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                statsborgerskap("SE", "DK")
            }
            avsenderLand = "NO"
            expectedResult = false
        },
        sedTestCase {
            name = "Norge er nevnt i arbeidssted.address.land"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                statsborgerskap("US")
                arbeidssted("NO")
            }
            avsenderLand = "NO"
            expectedResult = false
            reason = "Norge er nevnt som arbeidssted"
        },
        sedTestCase {
            name = "Norge er nevnt som i arbeidsland.land"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                statsborgerskap("US")
                arbeidsland("NO")
            }
            avsenderLand = "NO"
            expectedResult = false
            reason = "Norge er nevnt som arbeidssted"
        },
        sedTestCase {
            name = "Norge er nevnt som land i arbeidsland.arbeidssted.address.land"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                statsborgerskap("US")
                arbeidsland(arbeidsstedLand = listOf("NO"))
            }
            avsenderLand = "NO"
            expectedResult = false
            reason = "Norge er nevnt som arbeidssted"
        },
        sedTestCase {
            name = "Avsender er fra godkjent land for unntak"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                statsborgerskap("US")
            }
            avsenderLand = "CH"
            expectedResult = false
        },
        sedTestCase {
            name = "Tredjelandsborger uten Norge som arbeidssted"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                statsborgerskap("US")
            }
            avsenderLand = "FR"
            expectedResult = true
        },
        sedTestCase {
            name = "Ingen person funnet"
            sed {
                type = SedType.A003
                vedtakLand = "SE"
                utenPerson()
            }
            avsenderLand = "NO"
            expectedResult = false
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
    var type: SedType = SedType.A003
    var vedtakLand: String = "SE"
    private var person: Person? = Person(statsborgerskap = listOf(Statsborgerskap(land = "US")))
    private var arbeidssted: List<Arbeidssted> = emptyList()
    private var arbeidsland: List<Arbeidsland> = emptyList()

    fun statsborgerskap(vararg landkoder: String) {
        this.person = Person(
            statsborgerskap = landkoder.map { Statsborgerskap(land = it) }
        )
    }

    fun norskPin(personnummer: String) {
        this.person = Person(
            statsborgerskap = listOf(Statsborgerskap(land = "US")),
            pin = listOf(Pin(land = "NO", identifikator = personnummer))
        )
    }

    fun arbeidssted(vararg landkoder: String) {
        this.arbeidssted = landkoder.map { Arbeidssted(adresse = Adresse(land = it)) }
    }

    fun arbeidsland(vararg landkoder: String) {
        this.arbeidsland = landkoder.map { Arbeidsland(land = it) }
    }

    fun arbeidsland(arbeidsstedLand: List<String>) {
        this.arbeidsland = listOf(
            Arbeidsland(
                arbeidssted = arbeidsstedLand.map { Arbeidssted(adresse = Adresse(land = it)) }
            )
        )
    }

    fun utenPerson() {
        this.person = null
    }

    fun build(): SED {
        val medlemskap = when (type) {
            SedType.A003 -> MedlemskapA003(
                vedtak = VedtakA003(land = vedtakLand)
            )

            else -> null
        }

        return SED(
            sedType = type.name,
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
    var name: String = ""
    var sedBuilder: SEDTestBuilder = SEDTestBuilder()
    var avsenderLand: String = "NO"
    var expectedResult: Boolean = false
    var reason: String = ""

    fun sed(configure: SEDTestBuilder.() -> Unit) = apply {
        sedBuilder.apply(configure)
    }

    fun build(): Arguments.ArgumentSet =
        argumentSet(name, sedBuilder.build(), avsenderLand, expectedResult, reason.ifBlank { name })
}

fun sedTestCase(configure: TestCaseBuilder.() -> Unit) =
    TestCaseBuilder().apply(configure).build()
