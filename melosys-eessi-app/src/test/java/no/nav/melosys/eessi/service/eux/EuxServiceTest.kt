package no.nav.melosys.eessi.service.eux

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBeEmpty
import io.mockk.*
import no.nav.melosys.eessi.integration.eux.rina_api.Aksjoner
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer
import no.nav.melosys.eessi.integration.eux.rina_api.EuxRinasakerConsumer
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon
import no.nav.melosys.eessi.metrikker.BucMetrikker
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.SedVedlegg
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Conversation
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.exception.IntegrationException
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.models.sed.SED
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class EuxServiceTest {

    private val euxConsumer = mockk<EuxConsumer>()
    private val euxRinasakerConsumer = mockk<EuxRinasakerConsumer>()
    private val bucMetrikker = mockk<BucMetrikker>()
    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private lateinit var euxService: EuxService

    @BeforeEach
    fun setup() {
        euxService = EuxService(euxConsumer, bucMetrikker, euxRinasakerConsumer)
    }

    @Test
    fun `hentSed forventKonsumentKall`() {
        every { euxConsumer.hentSed("123123123", "12345") } returns SED()

        euxService.hentSed("123123123", "12345")

        verify { euxConsumer.hentSed("123123123", "12345") }
    }

    @Test
    fun `hentBucer forventKonsumentKall`() {
        val bucSearch = BucSearch(bucType = BucType.LA_BUC_01.name)
        every { euxConsumer.finnRinaSaker(BucType.LA_BUC_01.name, isNull()) } returns emptyList()

        euxService.hentBucer(bucSearch)

        verify { euxConsumer.finnRinaSaker(BucType.LA_BUC_01.name, isNull()) }
    }

    @Test
    fun `hentBuc forventKonsumentKall`() {
        every { euxConsumer.hentBUC(any()) } returns BUC()

        euxService.hentBuc("123123123")

        verify { euxConsumer.hentBUC("123123123") }
    }

    @Test
    fun `finnBuc integrasjonsfeil tomRespons`() {
        every { euxConsumer.hentBUC("123123123") } throws IntegrationException("err")

        val result = euxService.finnBUC("123123123")

        result.shouldBeEmpty()
    }

    @Test
    fun `genererPdfFraSed forventKonsumentkall`() {
        every { euxConsumer.genererPdfFraSed(any()) } returns "pdf".toByteArray()

        euxService.genererPdfFraSed(SED())

        verify { euxConsumer.genererPdfFraSed(any()) }
    }

    @Test
    fun `opprettBucOgSed forventRinaSaksnummer`() {
        every { euxConsumer.opprettBUC(any()) } returns OPPRETTET_BUC_ID
        every { euxConsumer.opprettSed(OPPRETTET_BUC_ID, any()) } returns OPPRETTET_SED_ID
        every { euxConsumer.leggTilVedleggMultipart(OPPRETTET_BUC_ID, OPPRETTET_SED_ID, "pdf", any()) } returns "123"
        every { euxConsumer.settMottakere(any(), any()) } returns Unit
        every { bucMetrikker.bucOpprettet(any()) } returns Unit
        val bucType = BucType.LA_BUC_01
        val mottakere = listOf("SE:123")
        val sed = SED()
        val vedlegg = setOf(SedVedlegg("filen min", "pdf".toByteArray()))

        val opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakere, sed, vedlegg)

        verify { euxConsumer.opprettBUC(bucType.name) }
        verify { euxConsumer.opprettSed(OPPRETTET_BUC_ID, sed) }
        verify { euxConsumer.leggTilVedleggMultipart(OPPRETTET_BUC_ID, OPPRETTET_SED_ID, "pdf", any()) }
        opprettBucOgSedResponse.rinaSaksnummer shouldBe OPPRETTET_BUC_ID
    }

    @Test
    fun `opprettOgSendSed medRinaSaksnummer forventKonsumentKall`() {
        every { euxConsumer.opprettSed(OPPRETTET_BUC_ID, any()) } returns OPPRETTET_SED_ID
        every { euxConsumer.hentBucHandlinger(OPPRETTET_BUC_ID) } returns listOf("SedId SedType ${Aksjoner.CREATE.hentHandling()}")
        every { euxConsumer.hentSedHandlinger(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) } returns setOf(Aksjoner.SEND.hentHandling())
        every { euxConsumer.sendSed(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) } just Runs

        euxService.opprettOgSendSed(SED(), OPPRETTET_BUC_ID)

        verify { euxConsumer.sendSed(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) }
    }

    @Test
    fun `opprettOgSendSedMedHandlingSjekk medIngenMuligBucHandlingCreate forventKasterException`() {
        every { euxConsumer.hentBucHandlinger(OPPRETTET_BUC_ID) } returns listOf("SedId SedType ${Aksjoner.READ.hentHandling()}")

        shouldThrow<ValidationException> {
            euxService.opprettOgSendSed(SED(), OPPRETTET_BUC_ID)
        }.message shouldBe "Kan ikke gjøre handling ${Aksjoner.CREATE.hentHandling()} på BUC $OPPRETTET_BUC_ID, ugyldig handling i Rina"
    }

    @Test
    fun `opprettOgSendSedMedHandlingSjekk medTomListeBucHandling forventKasterException`() {
        every { euxConsumer.hentBucHandlinger(OPPRETTET_BUC_ID) } returns listOf("SedId SedType ${Aksjoner.READ.hentHandling()}")

        shouldThrow<ValidationException> {
            euxService.opprettOgSendSed(SED(), OPPRETTET_BUC_ID)
        }.message shouldBe "Kan ikke gjøre handling ${Aksjoner.CREATE.hentHandling()} på BUC $OPPRETTET_BUC_ID, ugyldig handling i Rina"
    }

    @Test
    fun `opprettOgSendSedMedHandlingSjekk medTomtSedHandling forventKasterException`() {
        every { euxConsumer.opprettSed(OPPRETTET_BUC_ID, any()) } returns OPPRETTET_SED_ID
        every { euxConsumer.hentBucHandlinger(OPPRETTET_BUC_ID) } returns listOf("SedId SedType ${Aksjoner.CREATE.hentHandling()}")
        every { euxConsumer.hentSedHandlinger(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) } returns emptyList()

        val sed = SED()
        val exception = shouldThrow<ValidationException> {
            euxService.opprettOgSendSed(sed, OPPRETTET_BUC_ID)
        }
        exception.message shouldBe "Kan ikke sende SED på BUC $OPPRETTET_BUC_ID, ugyldig handling ${Aksjoner.SEND.hentHandling()} i Rina"
    }

    @Test
    fun `opprettOgSendSedMedHandlingSjekk medUgyldigSedHandling kasterIntegrationException`() {
        every { euxConsumer.opprettSed(OPPRETTET_BUC_ID, any()) } returns OPPRETTET_SED_ID
        every { euxConsumer.hentBucHandlinger(OPPRETTET_BUC_ID) } returns listOf("SedID Sedtype ${Aksjoner.CREATE.hentHandling()}")
        every { euxConsumer.hentSedHandlinger(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) } returns setOf(Aksjoner.READ.hentHandling())

        val sed = SED()
        val exception = shouldThrow<ValidationException> {
            euxService.opprettOgSendSed(sed, OPPRETTET_BUC_ID)
        }
        exception.message shouldBe "Kan ikke sende SED på BUC $OPPRETTET_BUC_ID, ugyldig handling ${Aksjoner.SEND.hentHandling()} i Rina"
    }

    @Test
    fun `opprettOgSendSedMedHandlingSjekk medFlereHandlinger forventKonsumentKall`() {
        every { euxConsumer.opprettSed(OPPRETTET_BUC_ID, any()) } returns OPPRETTET_SED_ID
        every { euxConsumer.hentBucHandlinger(OPPRETTET_BUC_ID) } returns listOf("test test ${Aksjoner.CREATE.hentHandling()}")
        every { euxConsumer.hentSedHandlinger(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) } returns listOf(
            Aksjoner.CLOSE.hentHandling(),
            Aksjoner.SEND.hentHandling(),
            Aksjoner.SEND.hentHandling()
        )
        every { euxConsumer.sendSed(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) } just Runs

        euxService.opprettOgSendSed(SED(), OPPRETTET_BUC_ID)

        verify { euxConsumer.sendSed(OPPRETTET_BUC_ID, OPPRETTET_SED_ID) }
    }

    @Test
    fun `hentRinaUrl medSaksnummer verifiserEuxConsumerKall`() {
        every { euxConsumer.hentRinaUrl("1234") } returns "https://rina-host-url.local/portal/#/caseManagement/1234"

        euxService.hentRinaUrl("1234")

        verify { euxConsumer.hentRinaUrl("1234") }
    }

    @Test
    fun `hentRinaUrl medRinaSaksnummer forventUrl`() {
        val rinaSak = "12345"
        val RINA_MOCK_URL = "https://rina-host-url.local"
        every { euxConsumer.hentRinaUrl(rinaSak) } returns "$RINA_MOCK_URL/portal/#/caseManagement/$rinaSak"
        val expectedUrl = "$RINA_MOCK_URL/portal/#/caseManagement/$rinaSak"

        val resultUrl = euxService.hentRinaUrl(rinaSak)

        resultUrl shouldBe expectedUrl
    }

    @Test
    fun `hentRinaUrl utenRinaSaksnummer forventException`() {
        val exception = shouldThrow<IllegalArgumentException> {
            euxService.hentRinaUrl(null)
        }

        exception.message shouldBe "Trenger rina-saksnummer for å opprette url til rina"
    }

    @Test
    fun `sendSed forventKonsumentKall`() {
        val rinaSaksnummer = "123"
        val dokumentId = "332211"
        val sedtype = SedType.A003.name
        every { euxConsumer.hentSedHandlinger(rinaSaksnummer, dokumentId) } returns setOf(Aksjoner.SEND.hentHandling())
        every { euxConsumer.sendSed(rinaSaksnummer, dokumentId) } just Runs

        euxService.sendSed(rinaSaksnummer, dokumentId, sedtype)

        verify { euxConsumer.sendSed(rinaSaksnummer, dokumentId) }
    }

    @Test
    fun `hentMottakerinstitusjoner laBuc04LandSverige forventEnInstitusjon`() {
        every { euxConsumer.hentInstitusjoner(BucType.LA_BUC_04.name, isNull()) } returns hentInstitusjoner()

        val institusjoner = euxService.hentMottakerinstitusjoner(BucType.LA_BUC_04.name, listOf("SE"))

        institusjoner.shouldHaveSize(1)
        institusjoner[0].akronym shouldBe "FK Sverige-TS70"
        verify { euxConsumer.hentInstitusjoner(BucType.LA_BUC_04.name, isNull()) }
    }

    @Test
    fun `hentMottakerinstitusjoner sBuc18LandSverige forventIngenInstitusjoner`() {
        every { euxConsumer.hentInstitusjoner(BucType.LA_BUC_04.name, isNull()) } returns hentInstitusjoner()
        every { euxConsumer.hentInstitusjoner("S_BUC_24", isNull()) } returns emptyList()

        val institusjoner = euxService.hentMottakerinstitusjoner("S_BUC_24", listOf("SE"))

        institusjoner.shouldBeEmpty()
    }

    @Test
    fun `hentMottakerinstitusjoner laBuc04LandGB forventEnInstitusjon`() {
        every { euxConsumer.hentInstitusjoner(BucType.LA_BUC_04.name, isNull()) } returns hentInstitusjoner()

        val institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_04", listOf("GB"))

        institusjoner.shouldHaveSize(1)
        institusjoner[0].akronym shouldBe "FK UK-TITTEI"
        institusjoner[0].landkode shouldBe "GB"
    }

    @Test
    fun `hentMottakerinstitusjoner laBuc04LandGR forventEnInstitusjon`() {
        every { euxConsumer.hentInstitusjoner(BucType.LA_BUC_04.name, isNull()) } returns hentInstitusjoner()

        val institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_04", listOf("GR"))

        institusjoner.shouldHaveSize(1)
        institusjoner[0].akronym shouldBe "FK EL-TITTEI"
        institusjoner[0].landkode shouldBe "GR"
    }

    @Test
    fun `sedErEndring medFlereConversations forventTrue`() {
        val sedID = "3333"
        val rinaSaksnummer = "333222111"
        val buc = lagBucMedDocument(rinaSaksnummer, sedID).apply {
            documents[0].conversations = listOf(Conversation(), Conversation())
        }
        every { euxConsumer.hentBUC(rinaSaksnummer) } returns buc

        val erEndring = euxService.sedErEndring(sedID, rinaSaksnummer)

        verify { euxConsumer.hentBUC(rinaSaksnummer) }
        erEndring shouldBe true
    }

    @Test
    fun `sedErEndring utenNoenConversations forventFalse`() {
        val sedID = "3556"
        val rinaSaksnummer = "54368"
        val buc = lagBucMedDocument(rinaSaksnummer, sedID).apply {
            documents[0].conversations = listOf(Conversation())
        }
        every { euxConsumer.hentBUC(rinaSaksnummer) } returns buc

        val erEndring = euxService.sedErEndring(sedID, rinaSaksnummer)

        verify { euxConsumer.hentBUC(rinaSaksnummer) }
        erEndring shouldBe false
    }

    @Test
    fun `sedErEndring utenSederForBuc forventFalse`() {
        val sedID = "33322"
        val buc = BUC(
            documents = listOf(
                Document(
                    id = sedID,
                    conversations = listOf(Conversation())
                ),
                Document(),
                Document()
            )
        )
        every { euxConsumer.hentBUC(any()) } returns buc

        val erEndring = euxService.sedErEndring(sedID, "123")

        verify { euxConsumer.hentBUC("123") }
        erEndring shouldBe false
    }

    private fun hentInstitusjoner(): List<Institusjon> =
        objectMapper.readValue(
            File(
                javaClass.classLoader.getResource(INSTITUSJONER_JSON)?.toURI()
                    ?: throw NotFoundException("Fant ikke $INSTITUSJONER_JSON")
            )
        )

    private fun lagBucMedDocument(rinaSaksnummer: String, sedID: String) = BUC(
        id = rinaSaksnummer,
        documents = listOf(
            Document(
                id = sedID,
                conversations = listOf(Conversation())
            )
        )
    )

    companion object {
        private const val OPPRETTET_BUC_ID = "123456"
        private const val OPPRETTET_SED_ID = "654321"
        private const val INSTITUSJONER_JSON = "institusjoner.json"

    }
}
