package no.nav.melosys.eessi.service.journalfoering

import com.google.common.collect.Lists
import io.github.benas.randombeans.api.EnhancedRandom
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.melosys.eessi.EnhancedRandomCreator
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class OpprettInngaaendeJournalpostServiceTest {

    private val journalpostService: JournalpostService = mockk()
    private val journalpostSedKoblingService: JournalpostSedKoblingService = mockk()

    private lateinit var opprettInngaaendeJournalpostService: OpprettInngaaendeJournalpostService

    private val enhancedRandom: EnhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom()
    private val sedMottatt = enhancedRandom.nextObject(SedHendelse::class.java).apply {
        bucType = BucType.LA_BUC_01.name
    }

    @BeforeEach
    fun setup() {
        opprettInngaaendeJournalpostService = OpprettInngaaendeJournalpostService(journalpostService, journalpostSedKoblingService)

        every { journalpostService.opprettInngaaendeJournalpost(any(), any(), any(), any()) } returns OpprettJournalpostResponse(
            JOURNALPOST_ID, Lists.newArrayList(
                OpprettJournalpostResponse.Dokument("123")
            ), null, null
        )
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

    private fun sedMedVedlegg(innhold: ByteArray): SedMedVedlegg =
        SedMedVedlegg(SedMedVedlegg.BinaerFil("", "", innhold), emptyList())

    companion object {
        private const val JOURNALPOST_ID = "123456789"
    }

}
