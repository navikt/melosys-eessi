package no.nav.melosys.eessi.service.mottak

import no.nav.melosys.eessi.models.sed.nav.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.mottak.SedA003UnntaksreglerForTredjelandsborgere.erNorgeNevntSomArbeidsSted

// Tester laget av Claude Sonnet 4
class SEDErNorgeNevntSomArbeidsStedTest {

    @Nested
    @DisplayName("erNorgeNevntSomArbeidsSted - nav.arbeidssted sjekk")
    inner class ArbeidsstedTest {

        @Test
        fun `skal returnere true når Norge finnes i nav arbeidssted`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(
                            adresse = Adresse(land = "NO"),
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere false når Norge ikke finnes i nav arbeidssted`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(
                            adresse = Adresse(land = "SE"),
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }

        @Test
        fun `skal returnere true når Norge finnes blant flere arbeidssted`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "SE")),
                        Arbeidssted(adresse = Adresse(land = "NO")),
                        Arbeidssted(adresse = Adresse(land = "DK"))
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere false når arbeidssted har null adresse`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = null)
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }

        @Test
        fun `skal returnere false når arbeidssted har adresse med null land`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(
                            adresse = Adresse(land = null)
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }
    }

    @Nested
    @DisplayName("erNorgeNevntSomArbeidsSted - nav.arbeidsland sjekk")
    inner class ArbeidslandTest {

        @Test
        fun `skal returnere true når Norge finnes i nav arbeidsland`() {
            val sed = SED(
                nav = Nav(
                    arbeidsland = listOf(
                        Arbeidsland(land = "NO")
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere false når Norge ikke finnes i nav arbeidsland`() {
            val sed = SED(
                nav = Nav(
                    arbeidsland = listOf(
                        Arbeidsland(land = "SE")
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }

        @Test
        fun `skal returnere true når Norge finnes blant flere arbeidsland`() {
            val sed = SED(
                nav = Nav(
                    arbeidsland = listOf(
                        Arbeidsland(land = "SE"),
                        Arbeidsland(land = "NO"),
                        Arbeidsland(land = "DK")
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere true når Norge finnes i arbeidssted under arbeidsland`() {
            val sed = SED(
                nav = Nav(
                    arbeidsland = listOf(
                        Arbeidsland(
                            land = "SE",
                            arbeidssted = listOf(
                                Arbeidssted(adresse = Adresse(land = "NO"))
                            )
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere true når Norge finnes i arbeidssted under flere arbeidsland`() {
            val sed = SED(
                nav = Nav(
                    arbeidsland = listOf(
                        Arbeidsland(
                            land = "SE",
                            arbeidssted = listOf(
                                Arbeidssted(adresse = Adresse(land = "DK"))
                            )
                        ),
                        Arbeidsland(
                            land = "FI",
                            arbeidssted = listOf(
                                Arbeidssted(adresse = Adresse(land = "NO"))
                            )
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere false når arbeidsland har null land`() {
            val sed = SED(
                nav = Nav(
                    arbeidsland = listOf(
                        Arbeidsland(land = null)
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }
    }

    @Nested
    @DisplayName("erNorgeNevntSomArbeidsSted - kombinerte scenarioer")
    inner class KombinerteScenarioerTest {

        @Test
        fun `skal returnere true når Norge finnes i både arbeidssted og arbeidsland`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "NO"))
                    ),
                    arbeidsland = listOf(
                        Arbeidsland(land = "NO")
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere true når Norge kun finnes i arbeidssted (ikke i arbeidsland)`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "NO"))
                    ),
                    arbeidsland = listOf(
                        Arbeidsland(land = "SE")
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere true når Norge kun finnes i arbeidsland (ikke i arbeidssted)`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "SE"))
                    ),
                    arbeidsland = listOf(
                        Arbeidsland(land = "NO")
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }

        @Test
        fun `skal returnere false når Norge ikke finnes noen steder`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "SE"))
                    ),
                    arbeidsland = listOf(
                        Arbeidsland(
                            land = "DK",
                            arbeidssted = listOf(
                                Arbeidssted(adresse = Adresse(land = "FI"))
                            )
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }
    }

    @Nested
    @DisplayName("erNorgeNevntSomArbeidsSted - edge cases")
    inner class EdgeCasesTest {

        @Test
        fun `skal returnere false når nav er null`() {
            val sed = SED(nav = null)

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }

        @Test
        fun `skal returnere false når nav arbeidssted er null`() {
            val sed = SED(
                nav = Nav(arbeidssted = null, arbeidsland = null)
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }

        @Test
        fun `skal returnere false når nav arbeidssted er tom liste`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = emptyList(),
                    arbeidsland = emptyList()
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe false
        }

        @Test
        fun `skal kunne søke etter annet land enn Norge`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "SE"))
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted("SE") shouldBe true
            sed.erNorgeNevntSomArbeidsSted("NO") shouldBe false
        }

        @Test
        fun `skal håndtere kompleks struktur med mange nivåer`() {
            val sed = SED(
                nav = Nav(
                    arbeidssted = listOf(
                        Arbeidssted(adresse = Adresse(land = "SE")),
                        Arbeidssted(adresse = null),
                        Arbeidssted(adresse = Adresse(land = null))
                    ),
                    arbeidsland = listOf(
                        Arbeidsland(
                            land = "DK",
                            arbeidssted = listOf(
                                Arbeidssted(adresse = Adresse(land = "FI")),
                                Arbeidssted(adresse = null)
                            )
                        ),
                        Arbeidsland(
                            land = null,
                            arbeidssted = listOf(
                                Arbeidssted(adresse = Adresse(land = "NO"))
                            )
                        )
                    )
                )
            )

            sed.erNorgeNevntSomArbeidsSted() shouldBe true
        }
    }
}
