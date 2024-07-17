package no.nav.melosys.eessi.service.journalfoering

import com.google.common.collect.Lists
import io.github.benas.randombeans.api.EnhancedRandom
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import no.nav.melosys.eessi.EnhancedRandomCreator
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse
import no.nav.melosys.eessi.integration.sak.Sak
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class OpprettInngaaendeJournalpostServiceTest {

    private val journalpostService: JournalpostService = mockk()
    private val saksrelasjonService: SaksrelasjonService = mockk()
    private val journalpostSedKoblingService: JournalpostSedKoblingService = mockk()

    private lateinit var opprettInngaaendeJournalpostService: OpprettInngaaendeJournalpostService

    private val enhancedRandom: EnhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom()

    private lateinit var sedMottatt: SedHendelse
    private val JOURNALPOST_ID = "11223344"
    private val GSAK_SAKSNUMMER = "123"

    @BeforeEach
    fun setup() {
        opprettInngaaendeJournalpostService = OpprettInngaaendeJournalpostService(saksrelasjonService, journalpostService, journalpostSedKoblingService)
        sedMottatt = enhancedRandom.nextObject(SedHendelse::class.java).apply {
            bucType = BucType.LA_BUC_01.name
        }

        val response = OpprettJournalpostResponse(JOURNALPOST_ID, Lists.newArrayList(
            OpprettJournalpostResponse.Dokument("123")), null, null)
        every { journalpostService.opprettInngaaendeJournalpost(any(), any(), any(), any()) } returns response
    }

    @Test
    fun arkiverInngaaendeSedHentSakinformasjon_journalpostOpprettet_forventMottattJournalpostID() {
        val sak = enhancedRandom.nextObject(Sak::class.java).apply {
            id = GSAK_SAKSNUMMER
        }
        every { saksrelasjonService.finnArkivsakForRinaSaksnummer(any()) } returns Optional.of(sak)
        every { journalpostSedKoblingService.lagre(any(), any(), any(), any(), any(), any()) } returns mockk()

        val sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, sedMedVedlegg(ByteArray(0)), "123")

        sakInformasjon shouldNotBe null
        sakInformasjon.journalpostId shouldBe JOURNALPOST_ID
        sakInformasjon.gsakSaksnummer shouldBe GSAK_SAKSNUMMER

        verify(exactly = 1) { journalpostService.opprettInngaaendeJournalpost(any(), any(), any(), any()) }
        verify {
            journalpostSedKoblingService.lagre(JOURNALPOST_ID, sedMottatt.rinaSakId,
                sedMottatt.rinaDokumentId, sedMottatt.rinaDokumentVersjon,
                sedMottatt.bucType, sedMottatt.sedType)
        }
    }

    @Test
    fun arkiverInngaaendeSedUtenBruker_journalpostOpprettet_forventReturnerJournalpostID() {
        every { journalpostSedKoblingService.lagre(any(), any(), any(), any(), any(), any()) } returns mockk()

        val journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(sedMottatt, sedMedVedlegg(ByteArray(0)), "123321")

        journalpostID shouldBe JOURNALPOST_ID

        verify {
            journalpostSedKoblingService.lagre(any(), any(), any(), any(), any(), any())
            journalpostService.opprettInngaaendeJournalpost(any(), isNull(), any(), any())
        }
    }

    private fun sedMedVedlegg(innhold: ByteArray): SedMedVedlegg {
        return SedMedVedlegg(SedMedVedlegg.BinaerFil("", "", innhold), emptyList())
    }
}
