package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver
import no.nav.melosys.eessi.models.sed.nav.VedtakA003
import org.junit.jupiter.api.Test

internal class SedGrunnlagMapperA003Test {
    private val sedGrunnlagMapper = SedGrunnlagMapperA003()

    @Test
    fun map_medUtfyltNav_forventVerdier() {
        val sedGrunnlagDto = sedGrunnlagMapper.map(hentSed())

        sedGrunnlagDto.shouldNotBeNull()
        sedGrunnlagDto.sedType shouldBe "A003"

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
            sedGrunnlagDto.arbeidssteder.shouldNotBeNull().shouldHaveSize(2).run {
                first().run {
                    navn shouldBe "Testarbeidsstednavn"
                    fysisk shouldBe false
                    hjemmebase shouldBe "Testarbeidsstedbase"
                }
                last().run {
                    navn shouldBe "Testarbeidsstednavn2"
                    fysisk shouldBe true
                    hjemmebase shouldBe "Testarbeidsstedbase2"
                }
            }
        }

        withClue("Arbeidssteder har rette adresser") {
            sedGrunnlagDto.arbeidssteder.shouldNotBeNull().shouldHaveSize(2).run {
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
                        postnr.shouldBeNull()
                        poststed shouldBe "Testarbeidsstedby2"
                        region.shouldBeNull()
                        gateadresse shouldBe "Testarbeidsstedgate2 Testarbeidsstedbygning2"
                    }
                }
            }
        }

        withClue("Arbeidsgivende virksomheter har rett info") {
            sedGrunnlagDto.arbeidsgivendeVirksomheter.shouldNotBeNull().shouldHaveSize(3).toList().run {
                get(0).run {
                    navn shouldBe "EQUINOR ASA"
                    orgnr shouldBe "923609016"
                }
                get(1).run {
                    navn shouldBe "adf"
                    orgnr shouldBe "123321"
                }
                get(2).run {
                    navn shouldBe "swe"
                    orgnr shouldBe "123"
                }
            }
        }

        withClue("Arbeidsgivende virksomheter har rette adresser") {
            sedGrunnlagDto.arbeidsgivendeVirksomheter.shouldNotBeNull().shouldHaveSize(3).toList().run {
                get(0).run {
                    adresse.shouldNotBeNull().run {
                        land shouldBe "BE"
                        postnr shouldBe "4035"
                        poststed shouldBe "STAVANGER"
                        gateadresse shouldBe "Forusbeen 50"
                    }
                }
                get(1).run {
                    adresse.shouldNotBeNull().run {
                        land shouldBe "BE"
                        postnr.shouldBeNull()
                        poststed shouldBe "by"
                        gateadresse shouldBe ""
                    }
                }
                get(2).run {
                    adresse.shouldNotBeNull().run {
                        land shouldBe "SE"
                        postnr.shouldBeNull()
                        poststed shouldBe "stck"
                        gateadresse shouldBe ""
                    }
                }
            }
        }

        withClue("Selvstendige virksomheter har rett info") {
            sedGrunnlagDto.selvstendigeVirksomheter.shouldNotBeNull().single().run {
                navn shouldBe "Testselvstendignavn"
                orgnr shouldBe "Testselvstendignummer"
            }
        }

        withClue("Selvstendige virksomheter har rette adresser") {
            sedGrunnlagDto.selvstendigeVirksomheter.shouldNotBeNull().single().run {
                adresse.shouldNotBeNull().run {
                    land shouldBe "BG"
                    postnr shouldBe "Testselvstendigpostkode"
                    poststed shouldBe "Testselvstendigby"
                    region shouldBe "Testselvstendigregion"
                    gateadresse shouldBe "Testselvstendiggate Testselvstendigbygning"
                }
            }
        }
    }

    @Test
    fun map_ingenBostedsadresse_forventPostadresse() {
        val sed = hentSed()
        val adresse = sed.nav!!.bruker!!.adresse!![0]
        adresse.type = Adressetype.POSTADRESSE.adressetypeRina
        sed.nav!!.bruker!!.adresse = listOf(adresse)

        val bostedsadresse = sedGrunnlagMapper.map(sed).bostedsadresse

        bostedsadresse.shouldNotBeNull().run {
            adressetype shouldBe Adressetype.POSTADRESSE
            land shouldBe "BE"
            gateadresse shouldBe "Testgate Testbyggnavn"
        }
    }

    @Test
    fun map_ingenAdresse_forventTomAdresse() {
        val sed = hentSed()
        sed.nav!!.bruker!!.adresse = listOf()

        val bostedsadresse = sedGrunnlagMapper.map(sed).bostedsadresse

        bostedsadresse shouldBe Adresse()
    }

    @Test
    fun map_kunNorskIdent_forventTomListeAvUtenlandskeIdenter() {
        val sed = hentSed()
        val pin = sed.nav!!.bruker!!.person!!.pin.iterator().next()
        pin.land = "NO"
        sed.nav!!.bruker!!.person!!.pin = listOf(pin)

        val utenlandskIdent = sedGrunnlagMapper.map(sed).utenlandskIdent

        utenlandskIdent.shouldBeEmpty()
    }

    @Test
    fun map_ingenGate_forventKunBygning() {
        val sed = hentSed()
        val adresse = sed.nav!!.bruker!!.adresse!![0]
        adresse.gate = null
        sed.nav!!.bruker!!.adresse = listOf(adresse)

        val gateadresse = sedGrunnlagMapper.map(sed).bostedsadresse!!.gateadresse

        gateadresse shouldBe "Testbyggnavn"
    }

    @Test
    fun map_ingenBygning_forventKunGate() {
        val sed = hentSed()
        val adresse = sed.nav!!.bruker!!.adresse!![0]
        adresse.bygning = null
        sed.nav!!.bruker!!.adresse = listOf(adresse)

        val gateadresse = sedGrunnlagMapper.map(sed).bostedsadresse!!.gateadresse

        gateadresse shouldBe "Testgate"
    }

    @Test
    fun map_ingenArbeidsgiverAdresse_forventIkkeNorskArbeidsgiver() {
        val settTomAdresse = { arbeidsgiver: Arbeidsgiver -> arbeidsgiver.adresse = null }

        val sed = hentSed()
        sed.nav!!.arbeidsgiver!!.forEach(settTomAdresse)
        (sed.medlemskap as MedlemskapA003).andreland!!.arbeidsgiver!!.forEach(settTomAdresse)

        val norskeArbeidsgivendeVirksomheter = sedGrunnlagMapper.map(sed).norskeArbeidsgivendeVirksomheter

        norskeArbeidsgivendeVirksomheter.shouldBeEmpty()
    }

    @Test
    fun map_norskArbeidsgiverNullIdentifikator_forventIngenIdentifikator() {
        val sed = hentSed()
        (sed.medlemskap as MedlemskapA003).andreland!!.arbeidsgiver!!.iterator().next().identifikator = null

        val orgnr = sedGrunnlagMapper.map(sed).norskeArbeidsgivendeVirksomheter!!.iterator().next().orgnr

        orgnr.shouldBeNull()
    }

    @Test
    fun sedErEndring_ikkeOpprinneligVedtak_forventerErEndring_true() {
        val medlemskapA003 = lagA003MedlemskapForSedErEndringTest(IKKE_OPPRINNELIG_VEDTAK)

        val erEndring = sedGrunnlagMapper.sedErEndring(medlemskapA003)

        erEndring shouldBe true
    }

    @Test
    fun sedErEndring_opprinneligVedtak_forventerErEndring_true() {
        val medlemskapA003 = lagA003MedlemskapForSedErEndringTest(OPPRINNELIG_VEDTAK)

        val erEndring = sedGrunnlagMapper.sedErEndring(medlemskapA003)

        erEndring shouldBe false
    }

    companion object {
        private val IKKE_OPPRINNELIG_VEDTAK: String? = null
        private const val OPPRINNELIG_VEDTAK = "ja"

        private fun lagA003MedlemskapForSedErEndringTest(opprinneligVedtak: String?): MedlemskapA003 = MedlemskapA003().apply {
            vedtak = VedtakA003().apply {
                eropprinneligvedtak = opprinneligVedtak
            }
        }

        private fun hentSed(): SED =
            ObjectMapper().readValue(
                SedGrunnlagMapperA003Test::class.java.classLoader.getResource("mock/sedA003.json"),
                SED::class.java
            )
    }
}
