package no.nav.melosys.eessi.service.mottak

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.Called
import io.mockk.verify
import no.nav.melosys.eessi.integration.PersonFasade
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.BucIdentifiseringOppg
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedMottattHendelse
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.journalfoering.OpprettInngaaendeJournalpostService
import no.nav.melosys.eessi.service.oppgave.OppgaveService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SedMottakAdminServiceTest {

    @MockK(relaxed = true)
    private lateinit var euxService: EuxService

    @MockK
    private lateinit var personFasade: PersonFasade

    @MockK
    private lateinit var opprettInngaaendeJournalpostService: OpprettInngaaendeJournalpostService

    @MockK
    private lateinit var oppgaveService: OppgaveService

    @MockK(relaxed = true)
    private lateinit var sedMottattHendelseRepository: SedMottattHendelseRepository

    @MockK(relaxed = true)
    private lateinit var bucIdentifiseringOppgRepository: BucIdentifiseringOppgRepository

    private lateinit var sedMottakAdminService: SedMottakAdminService

    @BeforeEach
    fun setup() {
        sedMottakAdminService = SedMottakAdminService(
            euxService,
            sedMottattHendelseRepository,
            IdentifiseringsoppgaveService(
                euxService,
                personFasade,
                opprettInngaaendeJournalpostService,
                oppgaveService,
                sedMottattHendelseRepository,
                bucIdentifiseringOppgRepository
            )
        )
    }

    @Test
    fun `upublisert A-SED uten aapen oppgave oppretter journalpost og oppgave`() {
        gittASed(publisert = false)
        every { bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER) } returns mutableSetOf()
        gittOppgaveOpprettes(journalpostId = "JP-1", oppgaveId = "OPPG-1")

        val resultat = sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)

        resultat.oppgaveId shouldBe "OPPG-1"
        resultat.journalpostId shouldBe "JP-1"
        resultat.rinaSaksnummer shouldBe RINA_SAKSNUMMER
        resultat.tidligereOppgaver shouldBe emptyList()
        verify { opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()) }
        verify { bucIdentifiseringOppgRepository.save(any()) }
    }

    @Test
    fun `gjenbruker eksisterende journalpost`() {
        gittASed(publisert = false, journalpostId = "JP-EKSISTERENDE")
        every { bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER) } returns mutableSetOf()
        gittOppgaveOpprettes(journalpostId = "JP-NY", oppgaveId = "OPPG-2")

        val resultat = sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)

        resultat.journalpostId shouldBe "JP-EKSISTERENDE"
        verify(exactly = 0) { opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()) }
    }

    @Test
    fun `publisert A-SED kaster ValidationException`() {
        gittASed(publisert = true)

        shouldThrow<ValidationException> {
            sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)
        }

        verify { oppgaveService wasNot Called }
        verify { opprettInngaaendeJournalpostService wasNot Called }
    }

    @Test
    fun `aapen oppgave kaster ValidationException`() {
        gittASed(publisert = false)
        gittTidligereOppgave("5555", "OPPRETTET")

        shouldThrow<ValidationException> {
            sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)
        }

        verify { opprettInngaaendeJournalpostService wasNot Called }
    }

    @Test
    fun `ferdigstilt oppgave blokkerer ikke ny oppgave`() {
        gittASed(publisert = false)
        gittTidligereOppgave("5555", "FERDIGSTILT")
        gittOppgaveOpprettes(journalpostId = "JP-1", oppgaveId = "OPPG-NY")

        val resultat = sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)

        resultat.oppgaveId shouldBe "OPPG-NY"
        resultat.tidligereOppgaver.single().oppgaveId shouldBe "5555"
        resultat.tidligereOppgaver.single().status shouldBe "FERDIGSTILT"
        resultat.tidligereOppgaver.single().erÅpen shouldBe false
    }

    @Test
    fun `oppgave som ikke finnes i Oppgave blokkerer ikke ny oppgave`() {
        gittASed(publisert = false)
        every { bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER) } returns
            mutableSetOf(BucIdentifiseringOppg(1L, RINA_SAKSNUMMER, "9999", 1))
        every { oppgaveService.hentOppgave("9999") } throws NotFoundException("Fant ikke oppgave med id 9999")
        gittOppgaveOpprettes(journalpostId = "JP-1", oppgaveId = "OPPG-NY")

        val resultat = sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)

        resultat.oppgaveId shouldBe "OPPG-NY"
        resultat.tidligereOppgaver.single().status shouldBe IdentifiseringsoppgaveService.STATUS_FINNES_IKKE
        resultat.tidligereOppgaver.single().erÅpen shouldBe false
    }

    @Test
    fun `ingen A-SED kaster NotFoundException`() {
        val xSed = sedMottattHendelse(sedType = "X001")
        every { sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(RINA_SAKSNUMMER) } returns listOf(xSed)

        shouldThrow<NotFoundException> {
            sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)
        }
    }

    @Test
    fun `ingen hendelser kaster NotFoundException`() {
        every { sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(RINA_SAKSNUMMER) } returns emptyList()

        shouldThrow<NotFoundException> {
            sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(RINA_SAKSNUMMER)
        }
    }

    private fun gittASed(publisert: Boolean, journalpostId: String? = null) {
        val aSed = sedMottattHendelse(sedType = "A009").apply {
            this.publisertKafka = publisert
            this.journalpostId = journalpostId
        }
        every { sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(RINA_SAKSNUMMER) } returns listOf(aSed)
    }

    private fun gittTidligereOppgave(oppgaveId: String, status: String) {
        every { bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER) } returns
            mutableSetOf(BucIdentifiseringOppg(1L, RINA_SAKSNUMMER, oppgaveId, 1))
        every { oppgaveService.hentOppgave(oppgaveId) } returns HentOppgaveDto().apply { this.status = status }
    }

    private fun gittOppgaveOpprettes(journalpostId: String, oppgaveId: String) {
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0
        every { opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()) } returns journalpostId
        every { personFasade.opprettLenkeForRekvirering(any()) } returns "http://lenke.no"
        every { oppgaveService.opprettOppgaveTilIdOgFordeling(any(), any(), any(), any()) } returns oppgaveId
        every { bucIdentifiseringOppgRepository.save(any()) } returnsArgument 0
    }

    private fun sedMottattHendelse(sedType: String) = SedMottattHendelse.builder()
        .sedHendelse(SedHendelse().apply {
            navBruker = "ukjent"
            avsenderId = "SE:12345"
            mottakerId = "SE:12345"
            mottakerNavn = "ukjent"
            avsenderNavn = "ukjent"
            rinaSakId = RINA_SAKSNUMMER
            rinaDokumentId = "456"
            sedId = SED_ID
            this.sedType = sedType
            bucType = BucType.LA_BUC_02.name
            sektorKode = "LA"
        })
        .build()

    private fun opprettSED() = SED(
        nav = Nav(
            bruker = Bruker(
                person = Person(
                    statsborgerskap = listOf("NO", "SE").map { Statsborgerskap(land = it) },
                    foedselsdato = "1990-01-01"
                )
            )
        ),
        sedType = "A009",
        medlemskap = MedlemskapA009(
            vedtak = VedtakA009(
                gjelderperiode = Periode(
                    fastperiode = Fastperiode(startdato = "2019-05-01", sluttdato = "2019-12-01")
                )
            )
        )
    )

    companion object {
        private const val SED_ID = "555554444"
        private const val RINA_SAKSNUMMER = "12313213"
    }
}
