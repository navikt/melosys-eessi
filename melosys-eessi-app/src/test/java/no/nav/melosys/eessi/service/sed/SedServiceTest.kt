package no.nav.melosys.eessi.service.sed

import io.getunleash.FakeUnleash
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.SedVedlegg
import no.nav.melosys.eessi.models.buc.Action
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.exception.IntegrationException
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class SedServiceTest {

    @MockK
    lateinit var euxService: EuxService

    @MockK
    lateinit var saksrelasjonService: SaksrelasjonService

    lateinit var sendSedService: SedService
    private val fakeUnleash = FakeUnleash()

    private val RINA_ID = "aabbcc"

    @BeforeEach
    fun setup() {
        sendSedService = SedService(euxService, saksrelasjonService, fakeUnleash, 0L)
    }

    @Test
    fun `opprettBucOgSed - forvent Rina case id`() {
        val sedData = SedDataStub.getStub()
        val vedlegg = setOf(SedVedlegg("tittei", "pdf".toByteArray()))

        every { euxService.opprettBucOgSed(any(), any(), any(), any()) } returns OpprettBucOgSedResponse(RINA_ID, "123")
        every { euxService.hentRinaUrl(any()) } returns "URL"
        every { saksrelasjonService.lagreKobling(any(), any(), any()) } returns mockk<FagsakRinasakKobling>()
        every { euxService.sendSed(any(), any(), any()) } returns Unit

        val sedDto = sendSedService.opprettBucOgSed(sedData, vedlegg, BucType.LA_BUC_01, true, false)

        verify { euxService.opprettBucOgSed(BucType.LA_BUC_01, sedData.mottakerIder!!, any(), vedlegg) }
        sedDto.rinaSaksnummer shouldBe RINA_ID
    }

    @Test
    fun `opprettBucOgSed - send Sed kaster exception, forvent slett Buc og Sakrelasjon`() {
        val sedData = SedDataStub.getStub()

        every { euxService.opprettBucOgSed(any(), any(), any(), any()) } returns OpprettBucOgSedResponse(RINA_ID, "123")
        every { euxService.sendSed(any(), any(), any()) } throws IntegrationException("")
        every { euxService.slettBUC(RINA_ID) } returns Unit
        every { saksrelasjonService.lagreKobling(any(), any(), any()) } returns mockk<FagsakRinasakKobling>()
        every { saksrelasjonService.slettVedRinaId(RINA_ID) } returns Unit

        shouldThrow<IntegrationException> {
            sendSedService.opprettBucOgSed(sedData, emptyList(), BucType.LA_BUC_02, true, false)
        }

        verify { euxService.slettBUC(RINA_ID) }
        verify { saksrelasjonService.slettVedRinaId(RINA_ID) }
    }

    @Test
    fun `opprettBucOgSed - bruker med sensitive opplysninger, forvent sett SakSensitiv`() {
        val sedData = SedDataStub.getStub().apply {
            bruker!!.harSensitiveOpplysninger = true
        }

        every { euxService.opprettBucOgSed(any(), any(), any(), any()) } returns OpprettBucOgSedResponse(RINA_ID, "123")
        every { euxService.hentRinaUrl(any()) } returns "URL"
        every { saksrelasjonService.lagreKobling(123, RINA_ID, BucType.LA_BUC_02) } returns mockk<FagsakRinasakKobling>()
        every { euxService.settSakSensitiv(RINA_ID) } returns Unit
        every { euxService.sendSed(RINA_ID, "123", SedType.A003.name) } returns Unit

        val sedDto = sendSedService.opprettBucOgSed(sedData, emptyList(), BucType.LA_BUC_02, true, false)

        sedDto.rinaSaksnummer shouldBe RINA_ID
        verify { euxService.settSakSensitiv(RINA_ID) }
    }

    @Test
    fun `opprettBucOgSed - Sed eksisterer på Buc, forvent oppdater eksisterende Sed`() {
        val gsakSaksnummer = 123L
        val sedDataDto = SedDataStub.getStub().apply { this.gsakSaksnummer = gsakSaksnummer }

        val fagsakRinasakKobling = FagsakRinasakKobling(
            rinaSaksnummer = RINA_ID,
            gsakSaksnummer = gsakSaksnummer,
            bucType = BucType.LA_BUC_02
        )

        every { saksrelasjonService.finnVedGsakSaksnummerOgBucType(gsakSaksnummer, BucType.LA_BUC_02) } returns listOf(fagsakRinasakKobling)

        val buc = BUC(
            id = RINA_ID,
            bucVersjon = "v4.",
            status = "open",
            documents = listOf(
                Document(id = "docid12314", status = "empty", type = SedType.A003.name),
                Document(id = "docid321", status = "sent", type = SedType.A003.name)
            ),
            actions = listOf(
                Action(documentId = "docid12314", operation = "update"),
                Action(documentId = "docid321", operation = "update")
            )
        )

        every { euxService.finnBUC(RINA_ID) } returns Optional.of(buc)
        every { euxService.oppdaterSed(RINA_ID, "docid321", any()) } returns Unit
        every { euxService.sendSed(RINA_ID, "docid321", SedType.A003.name) } returns Unit
        every { euxService.hentRinaUrl(RINA_ID) } returns "URL"

        sendSedService.opprettBucOgSed(sedDataDto, emptyList(), BucType.LA_BUC_02, true, true)

        verify { euxService.oppdaterSed(eq(RINA_ID), eq("docid321"), any()) }
        verify(exactly = 0) { euxService.opprettBucOgSed(any(), any(), any(), any()) }
        verify { euxService.sendSed(any(), any(), any()) }
    }

    @Test
    fun `opprettBucOgSed - ingen Gsak Saksnummer, forvent MappingException`() {
        val sedData = SedDataStub.getStub().apply { gsakSaksnummer = null }

        shouldThrow<MappingException> {
            sendSedService.opprettBucOgSed(sedData, emptyList(), BucType.LA_BUC_04, true, false)
        }.message shouldContain "GsakId er påkrevd"
    }

    @Test
    fun `opprettBucOgSed - LA_BUC_01, forvent opprett ny Buc og Sed med URL`() {
        val sedData = SedDataStub.getStub()

        every { euxService.opprettBucOgSed(any(), any(), any(), any()) } returns OpprettBucOgSedResponse(RINA_ID, "123")
        every { euxService.hentRinaUrl(any()) } returns "URL"
        every { saksrelasjonService.lagreKobling(any(), any(), any()) } returns mockk<FagsakRinasakKobling>()

        val response = sendSedService.opprettBucOgSed(sedData, emptyList(), BucType.LA_BUC_01, false, false)

        verify { euxService.opprettBucOgSed(any(), any(), any(), any()) }
        verify { euxService.hentRinaUrl(RINA_ID) }
        verify(exactly = 0) { euxService.sendSed(any(), any(), any()) }

        response.rinaSaksnummer shouldBe RINA_ID
        response.rinaUrl shouldBe "URL"
    }

    @Test
    fun `send på eksisterende Buc, forvent metodekall`() {
        val buc = BUC(
            bucVersjon = "v4.1",
            actions = listOf(
                Action("A001", "A001", "111", "Read"),
                Action("A009", "A009", "222", "Create")
            )
        )

        every { euxService.hentBuc(any()) } returns buc
        every { euxService.opprettOgSendSed(any(), any()) } returns Unit

        val sedDataDto = SedDataStub.getStub()
        sendSedService.sendPåEksisterendeBuc(sedDataDto, "123", SedType.A009)

        verify { euxService.hentBuc(any()) }
        verify { euxService.opprettOgSendSed(any(), any()) }
    }

    @Test
    fun `genererPdfFraSed, forvent kall`() {
        val sedDataDto = SedDataStub.getStub()
        val mockPdf = "vi later som om dette er en pdf".toByteArray()

        every { euxService.genererPdfFraSed(any()) } returns mockPdf

        val pdf = sendSedService.genererPdfFraSed(sedDataDto, SedType.A001)

        verify { euxService.genererPdfFraSed(any()) }
        pdf shouldBe mockPdf
    }
}
