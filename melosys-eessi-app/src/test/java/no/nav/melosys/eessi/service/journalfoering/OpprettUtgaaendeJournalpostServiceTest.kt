package no.nav.melosys.eessi.service.journalfoering

import io.github.benas.randombeans.api.EnhancedRandom
import io.kotest.matchers.shouldBe
import io.mockk.*
import no.nav.melosys.eessi.EnhancedRandomCreator
import no.nav.melosys.eessi.identifisering.PersonIdentifisering
import no.nav.melosys.eessi.integration.PersonFasade
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse
import no.nav.melosys.eessi.integration.sak.Sak
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedSendtHendelse
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import no.nav.melosys.eessi.repository.SedSendtHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.oppgave.OppgaveService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class OpprettUtgaaendeJournalpostServiceTest {

    private val JOURNALPOST_ID = "123"

    private val saksrelasjonService: SaksrelasjonService = mockk()
    private val journalpostService: JournalpostService = mockk()
    private val euxService: EuxService = mockk()
    private val personFasade: PersonFasade = mockk()
    private val oppgaveService: OppgaveService = mockk()
    private val sedMetrikker: SedMetrikker = mockk()
    private val personIdentifisering: PersonIdentifisering = mockk()
    private val sedSendtHendelseRepository: SedSendtHendelseRepository = mockk()

    private lateinit var opprettUtgaaendeJournalpostService: OpprettUtgaaendeJournalpostService

    private val enhancedRandom: EnhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom()
    private val sedSendt: SedHendelse = enhancedRandom.nextObject(SedHendelse::class.java).apply {
        bucType = BucType.LA_BUC_01.name
        sektorKode = "LA"
    }

    @BeforeEach
    fun setup() {
        opprettUtgaaendeJournalpostService = OpprettUtgaaendeJournalpostService(
            saksrelasjonService,
            journalpostService,
            euxService,
            personFasade,
            oppgaveService,
            sedMetrikker,
            personIdentifisering,
            sedSendtHendelseRepository
        )

        every { euxService.hentSedMedVedlegg(any(), any()) } returns sedMedVedlegg(ByteArray(0))

        every { saksrelasjonService.finnArkivsakForRinaSaksnummer(any()) } returns Optional.of(
            enhancedRandom.nextObject(Sak::class.java)
        )
    }

    @Test
    fun arkiverUtgaaendeSed_forventId() {
        val response = OpprettJournalpostResponse(JOURNALPOST_ID, ArrayList(), "ENDELIG", null)
        every { journalpostService.opprettUtgaaendeJournalpost(any(), any(), any(), any()) } returns response
        every { personFasade.hentNorskIdent(any()) } returns "54321"

        val result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt)
        result shouldBe JOURNALPOST_ID
    }

    @Test
    fun arkiverUtgaaendeSed_ikkeEndelig_forventOpprettJfrOppgave() {
        val response = OpprettJournalpostResponse(JOURNALPOST_ID, ArrayList(), "MIDLERTIDIG", null)
        every { journalpostService.opprettUtgaaendeJournalpost(any(), any(), any(), any()) } returns response
        every { euxService.hentRinaUrl(any()) } returns "https://test.local"
        every { personFasade.hentAktoerId(any()) } returns "12345"
        every { personFasade.hentNorskIdent(any()) } returns "54321"
        every { oppgaveService.opprettUtgåendeJfrOppgave(any(), any(), any(), any()) } returns "brukes ikke"

        val result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt)

        verify { saksrelasjonService.finnArkivsakForRinaSaksnummer(any()) }
        verify { journalpostService.opprettUtgaaendeJournalpost(any(), any(), any(), any()) }
        verify { oppgaveService.opprettUtgåendeJfrOppgave(any(), any(), any(), any()) }

        result shouldBe JOURNALPOST_ID
    }

    @Test
    fun arkiverUtgaaendeSed_ingenSak_forventOpprettJfrOppgave() {
        val response = OpprettJournalpostResponse(JOURNALPOST_ID, ArrayList(), "ENDELIG", null)
        every { journalpostService.opprettUtgaaendeJournalpost(any(), any(), any(), any()) } returns response
        every { euxService.hentRinaUrl(any()) } returns "https://test.local"
        every { saksrelasjonService.finnArkivsakForRinaSaksnummer(any()) } returns Optional.empty()
        every { personFasade.hentAktoerId(any()) } returns "12345"
        every { oppgaveService.opprettUtgåendeJfrOppgave(any(), any(), any(), any()) } returns "brukes ikke"

        val journalpostId = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt)

        verify { saksrelasjonService.finnArkivsakForRinaSaksnummer(any()) }
        verify { journalpostService.opprettUtgaaendeJournalpost(any(), any(), any(), any()) }
        verify { oppgaveService.opprettUtgåendeJfrOppgave(any(), any(), any(), any()) }

        journalpostId shouldBe JOURNALPOST_ID
    }

    @Test
    fun behandleSedHendelse_harPid_forventOpprettJfrOppgave() {
        val response = OpprettJournalpostResponse(JOURNALPOST_ID, ArrayList(), "ENDELIG", null)
        every { journalpostService.opprettUtgaaendeJournalpost(any(), any(), any(), any()) } returns response
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of("12345")
        every { personFasade.hentAktoerId(any()) } returns "12345"
        every { euxService.hentRinaUrl(any()) } returns "https://test.local"
        every { saksrelasjonService.finnArkivsakForRinaSaksnummer(any()) } returns Optional.empty()
        every { euxService.hentSedMedRetry(any(), any()) } returns SED()
        every { oppgaveService.opprettUtgåendeJfrOppgave(any(), any(), any(), any()) } returns "brukes ikke"
        every { sedSendtHendelseRepository.findAllByRinaSaksnummerAndAndJournalpostIdIsNull(any()) } returns mutableListOf()
        every { sedMetrikker.sedSendt(any()) } returns Unit

        opprettUtgaaendeJournalpostService.behandleSedSendtHendelse(sedSendt)

        verify { oppgaveService.opprettUtgåendeJfrOppgave(any(), any(), any(), any()) }
    }

    @Test
    fun behandleSedHendelse_harIkkePid_forventIkkeOpprettJfrOppgave() {
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.empty()
        every { sedSendtHendelseRepository.save(any<SedSendtHendelse>()) } returns SedSendtHendelse()
        every { euxService.hentSedMedRetry(any(), any()) } returns SED()
        every { sedMetrikker.sedSendt(any()) } returns Unit

        opprettUtgaaendeJournalpostService.behandleSedSendtHendelse(sedSendt)

        verify { oppgaveService wasNot called }
        verify { sedSendtHendelseRepository.save(any<SedSendtHendelse>()) }
    }

    private fun sedMedVedlegg(innhold: ByteArray): SedMedVedlegg {
        return SedMedVedlegg(SedMedVedlegg.BinaerFil("", "", innhold), emptyList())
    }
}
