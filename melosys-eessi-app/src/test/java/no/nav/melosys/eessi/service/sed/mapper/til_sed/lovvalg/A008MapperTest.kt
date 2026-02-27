package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import io.getunleash.FakeUnleash
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.CDM_4_4
import no.nav.melosys.eessi.controller.dto.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA008
import no.nav.melosys.eessi.models.sed.nav.ArbeidIFlereLand
import no.nav.melosys.eessi.service.sed.SedDataStub
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule

class A008MapperTest {

    private lateinit var fakeUnleash: FakeUnleash
    private lateinit var a008Mapper: A008Mapper

    private val jsonMapper: JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().enable(KotlinFeature.NullIsSameAsDefault).build())
        .build()

    @BeforeEach
    fun setup() {
        fakeUnleash = FakeUnleash()
        a008Mapper = A008Mapper(fakeUnleash)
    }

    @Test
    fun `arbeid i flere land ukjent hvilke`() {
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            harFastArbeidssted = false
            arbeidsland = listOf()
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.nav.shouldNotBeNull().run {
            arbeidssted.shouldBeNull()
            harfastarbeidssted.shouldBeNull()
        }
    }

    @Test
    fun `map til SED med versjon 3 naar toggle er av`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            bruker.shouldNotBeNull().run {
                arbeidiflereland.shouldNotBeNull()
                    .shouldBeInstanceOf<ArbeidIFlereLand>().run {
                        yrkesaktivitet.shouldNotBeNull()
                            .startdato shouldBe "2020-01-01"
                        bosted.shouldNotBeNull()
                            .land shouldBe "SE"
                    }
            }

            sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().run {
                size shouldBe 1
                first().bosted.shouldBeNull()
            }
        }
    }

    @Test
    fun `map til SED med CDM 4_4 toggle paa har korrekt medlemskap-struktur`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            bruker.shouldNotBeNull().run {
                arbeidiflereland.shouldNotBeNull()
                    .shouldBeInstanceOf<List<*>>()
                @Suppress("UNCHECKED_CAST")
                (arbeidiflereland as List<ArbeidIFlereLand>).run {
                    size shouldBe 1
                    first().run {
                        yrkesaktivitet.shouldNotBeNull()
                            .startdato shouldBe "2020-01-01"
                        bosted.shouldNotBeNull()
                            .land shouldBe "SE"
                    }
                }
            }

            sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().size shouldBe 1
        }
    }

    @Test
    fun `formaal fra sedData mappes til SED naar toggle er paa`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal shouldBe "arbeid_flere_land"
        }
    }

    @Test
    fun `formaal endringsmelding mappes korrekt`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            a008Formaal = A008Formaal.ENDRINGSMELDING
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal shouldBe "endringsmelding"
        }
    }

    @Test
    fun `set til arbeid_flere_land naar toggle er paa men formaal mangler`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {}

        val sed: SED = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>()
            .formaal.shouldNotBeNull()
            .shouldBe("arbeid_flere_land")
    }

    @Test
    fun `formaal er null naar toggle er av selv om sedData har formaal`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            formaal.shouldBeNull()
        }
    }

    @Test
    fun `bosted-adresse ligger paa arbeidsland index 0 uansett land-match`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
            bostedsadresse = Adresse(poststed = "Stockholm", land = "SE", adressetype = Adressetype.BOSTEDSADRESSE)
            arbeidsland = listOf(
                no.nav.melosys.eessi.controller.dto.Arbeidsland(
                    land = "NO",
                    arbeidssted = listOf(Arbeidssted(adresse = Adresse(poststed = "Oslo", land = "NO"), fysisk = true, navn = "Jobb1"))
                ),
                no.nav.melosys.eessi.controller.dto.Arbeidsland(
                    land = "SE",
                    arbeidssted = listOf(Arbeidssted(adresse = Adresse(poststed = "Stockholm", land = "SE"), fysisk = true, navn = "Jobb2"))
                )
            )
        }

        val sed = a008Mapper.mapTilSed(sedData)

        val arbeidslandListe = sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull()
        arbeidslandListe.size shouldBe 2

        arbeidslandListe[0].run {
            land shouldBe "NO"
            bosted.shouldNotBeNull().adresse.shouldNotBeNull().run {
                by shouldBe "Stockholm"
                land shouldBe "SE"
            }
        }

        arbeidslandListe[1].run {
            land shouldBe "SE"
            bosted.shouldBeNull()
        }
    }

    @Test
    fun `bosted er null naar avklartBostedsland mangler`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = null
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().first().run {
            bosted.shouldBeNull()
        }
    }

    @Test
    fun `bosted-adresse settes med NA naar bostedsadresse mangler`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "PT"
            bostedsadresse = null
        }

        val sed = a008Mapper.mapTilSed(sedData)

        sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().first().run {
            bosted.shouldNotBeNull().adresse.shouldNotBeNull().run {
                land shouldBe "PT"
                by shouldBe "N/A"
            }
        }
    }

    @Test
    fun `companyNameVesselName settes paa arbeidssted adresse naar CDM 4_4`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json")

        val sed = a008Mapper.mapTilSed(sedData)

        sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().first().run {
            arbeidssted.first().run {
                navn shouldBe "MinJobb"
                adresse.shouldNotBeNull().navn shouldBe "MinJobb"
            }
        }
    }

    @Test
    fun `companyNameVesselName settes ikke paa arbeidssted adresse naar CDM 4_3`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json")

        val sed = a008Mapper.mapTilSed(sedData)

        sed.nav.shouldNotBeNull().arbeidsland.shouldNotBeNull().first().run {
            arbeidssted.first().run {
                navn shouldBe "MinJobb"
                adresse.shouldNotBeNull().navn.shouldBeNull()
            }
        }
    }

    @Test
    fun `CDM 4_4 serialiserer arbeidiflereland som array i JSON`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val sed = a008Mapper.mapTilSed(sedData)
        val json = jsonMapper.writeValueAsString(sed)
        val jsonTree = jsonMapper.readTree(json)

        val arbeidiflereland = jsonTree.path("medlemskap").path("bruker").path("arbeidiflereland")
        arbeidiflereland.isArray.shouldBeTrue()
        arbeidiflereland.size() shouldBe 1
        arbeidiflereland[0].path("bosted").path("land").asText() shouldBe "SE"
    }

    @Test
    fun `CDM 4_4 rund-tur serialisering-deserialisering bevarer array-struktur`() {
        fakeUnleash.enable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
            a008Formaal = A008Formaal.ARBEID_FLERE_LAND
        }

        val original = a008Mapper.mapTilSed(sedData)
        val json = jsonMapper.writeValueAsString(original)
        val deserialized = jsonMapper.readValue(json, SED::class.java)

        deserialized.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            bruker.shouldNotBeNull().run {
                // Etter deserialisering fra array blir dette List<LinkedHashMap> (Any?-type)
                arbeidiflereland.shouldNotBeNull()
                    .shouldBeInstanceOf<List<*>>()
                @Suppress("UNCHECKED_CAST")
                val items = arbeidiflereland as List<Map<String, Any?>>
                items.size shouldBe 1
                @Suppress("UNCHECKED_CAST")
                val bosted = items.first()["bosted"] as Map<String, Any?>
                bosted["land"] shouldBe "SE"
            }
        }
    }

    @Test
    fun `CDM 4_3 rund-tur serialisering-deserialisering bevarer objekt-struktur`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
        }

        val original = a008Mapper.mapTilSed(sedData)
        val json = jsonMapper.writeValueAsString(original)
        val deserialized = jsonMapper.readValue(json, SED::class.java)

        deserialized.medlemskap.shouldBeInstanceOf<MedlemskapA008>().run {
            bruker.shouldNotBeNull().run {
                // Etter deserialisering fra objekt blir dette LinkedHashMap (Any?-type)
                arbeidiflereland.shouldNotBeNull()
                    .shouldBeInstanceOf<Map<*, *>>()
                @Suppress("UNCHECKED_CAST")
                val map = arbeidiflereland as Map<String, Any?>
                @Suppress("UNCHECKED_CAST")
                val bosted = map["bosted"] as Map<String, Any?>
                bosted["land"] shouldBe "SE"
            }
        }
    }

    @Test
    fun `CDM 4_3 serialiserer arbeidiflereland som objekt i JSON`() {
        fakeUnleash.disable(CDM_4_4)
        val sedData = SedDataStub.getStub("mock/sedDataDtoStub.json") {
            avklartBostedsland = "SE"
        }

        val sed = a008Mapper.mapTilSed(sedData)
        val json = jsonMapper.writeValueAsString(sed)
        val jsonTree = jsonMapper.readTree(json)

        val arbeidiflereland = jsonTree.path("medlemskap").path("bruker").path("arbeidiflereland")
        arbeidiflereland.isObject.shouldBeTrue()
        arbeidiflereland.path("bosted").path("land").asText() shouldBe "SE"
    }
}
