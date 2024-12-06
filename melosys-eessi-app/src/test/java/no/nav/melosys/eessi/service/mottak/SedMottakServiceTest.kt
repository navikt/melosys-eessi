package no.nav.melosys.eessi.service.mottak

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.melosys.eessi.identifisering.BucIdentifisertService
import no.nav.melosys.eessi.identifisering.PersonIdentifisering
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto
import no.nav.melosys.eessi.integration.pdl.PDLService
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import no.nav.melosys.eessi.models.sed.nav.*
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.journalfoering.OpprettInngaaendeJournalpostService
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService
import no.nav.melosys.eessi.service.oppgave.OppgaveService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class SedMottakServiceTest {

    @MockK
    private lateinit var opprettInngaaendeJournalpostService: OpprettInngaaendeJournalpostService

    @MockK(relaxed = true)
    private lateinit var euxService: EuxService

    @MockK(relaxed = true)
    private lateinit var personIdentifisering: PersonIdentifisering

    @MockK
    private lateinit var pdlService: PDLService

    @MockK
    private lateinit var oppgaveService: OppgaveService

    @MockK(relaxed = true)
    private lateinit var sedMottattHendelseRepository: SedMottattHendelseRepository

    @MockK(relaxed = true)
    private lateinit var bucIdentifiseringOppgRepository: BucIdentifiseringOppgRepository

    @MockK(relaxed = true)
    private lateinit var bucIdentifisertService: BucIdentifisertService

    @MockK
    private lateinit var journalpostSedKoblingService: JournalpostSedKoblingService

    @MockK(relaxed = true)
    private lateinit var sedMetrikker: SedMetrikker

    @MockK
    private lateinit var saksrelasjonService: SaksrelasjonService

    private lateinit var sedMottakService: SedMottakService


    @BeforeEach
    fun setup() {
        sedMottakService = SedMottakService(
            euxService,
            pdlService,
            opprettInngaaendeJournalpostService,
            oppgaveService,
            sedMottattHendelseRepository,
            bucIdentifiseringOppgRepository,
            journalpostSedKoblingService,
            sedMetrikker,
            personIdentifisering,
            bucIdentifisertService,
            saksrelasjonService,
            "1"
        )
        val rinasakKobling = FagsakRinasakKobling(rinaSaksnummer = "test", gsakSaksnummer = 111111111, bucType = BucType.LA_BUC_02)
        every { saksrelasjonService.finnVedRinaSaksnummer(any()) } returns Optional.of(rinasakKobling)
    }

    @Test
    fun `behandleSed finnerIkkePerson OppgaveOpprettes`() {
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0
        every {
            opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any())
        } returns "ignorer"
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.empty()
        every { pdlService.opprettLenkeForRekvirering(any()) } returns "http://lenke.no"
        every { oppgaveService.opprettOppgaveTilIdOgFordeling(any(), any(), any(), any()) } returns "ignorer"
        every { bucIdentifiseringOppgRepository.save(any()) } returns mockk()
        val sedHendelse = sedHendelseUtenBruker()
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()


        sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)


        verify { euxService.hentSedMedRetry(any(), any()) }
        verify { personIdentifisering.identifiserPerson(any(), any()) }
        verify { opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()) }
        verify { oppgaveService.opprettOppgaveTilIdOgFordeling(any(), any(), any(), any()) }
        verify(exactly = 2) { sedMottattHendelseRepository.save(any()) }
        verify(exactly = 0) { bucIdentifisertService.lagreIdentifisertPerson(any(), any()) }
    }

    @Test
    fun `behandleSed finnerIkkePerson oppgaveOpprettesNårIkkeASed`() {
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.empty()
        val sedHendelse = sedHendelseUtenBruker().apply { sedType = "H001" }
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()


        sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)


        verify { euxService.hentSedMedRetry(any(), any()) }
        verify { personIdentifisering.identifiserPerson(any(), any()) }
        verify { sedMottattHendelseRepository.findBySedID(any()) }
        verify { opprettInngaaendeJournalpostService wasNot Called }
        verify { oppgaveService wasNot Called }
    }

    @Test
    fun `behandleSed finnerPerson forventPersonIdentifisertEvent`() {
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0
        val sedHendelse = sedHendelseMedBruker()


        sedMottakService.behandleSedMottakHendelse(SedMottattHendelse.builder().sedHendelse(sedHendelse).build())


        verify { euxService.hentSedMedRetry(any(), any()) }
        verify { personIdentifisering.identifiserPerson(any(), any()) }
        verify { opprettInngaaendeJournalpostService wasNot Called }
        verify { oppgaveService wasNot Called }
        verify { sedMottattHendelseRepository.save(any()) }
        verify { bucIdentifisertService.lagreIdentifisertPerson(sedHendelse.rinaSakId, IDENT) }
    }

    @Test
    fun `behandleSed ikkeIdentifisertÅpenOppgaveFinnes oppretterIkkeNyOppgaveEllerJournalpost`() {
        val oppgaveID = "5555"
        val bucIdentifiseringOppg = BucIdentifiseringOppg(1L, RINA_SAKSNUMMER, oppgaveID, 1)
        every { bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER) } returns mutableSetOf(
            bucIdentifiseringOppg
        )
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0
        every { oppgaveService.hentOppgave(oppgaveID) } returns HentOppgaveDto().apply { status = "OPPRETTET" }
        val sedMottattHendelse: SedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelseMedBruker()).build()


        sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)


        verify { opprettInngaaendeJournalpostService wasNot Called }
        verify { oppgaveService wasNot Called }
        verify { bucIdentifisertService wasNot Called }
    }

    @Test
    fun `behandleSed sedAlleredeBehandlet behandlerIkkeVidere`() {
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelseMedBruker()).build()
        every { sedMottattHendelseRepository.findBySedID(SED_ID) } returns Optional.of(sedMottattHendelse)


        sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)


        verify { euxService wasNot Called }
        verify { personIdentifisering wasNot Called }
        verify { opprettInngaaendeJournalpostService wasNot Called }
        verify { oppgaveService wasNot Called }
    }

    @Test
    fun `behandleSed erHbuc_sedTypeH002 behandlerVidere`() {
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelseMedBruker()).build()
        sedMottattHendelse.sedHendelse.sedType = SedType.H002.name
        sedMottattHendelse.sedHendelse.bucType = BucType.H_BUC_01.name
        sedMottattHendelse.sedHendelse.sektorKode = "H"

        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0
        sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)

        verify { euxService.hentSedMedRetry(any(), any()) }
        verify { personIdentifisering.identifiserPerson(any(), any()) }
    }

    @Test
    fun `behandleSed erIkkeLaBucEllerHBuc behandlerIkkeVidere`() {
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelseMedBruker()).build()
        sedMottattHendelse.sedHendelse.sektorKode = "A"
        every { sedMottattHendelseRepository.findBySedID(SED_ID) } returns Optional.of(sedMottattHendelse)

        sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)

        verify { euxService wasNot Called }
        verify { personIdentifisering wasNot Called }
        verify { opprettInngaaendeJournalpostService wasNot Called }
        verify { oppgaveService wasNot Called }
    }

    @Test
    fun `behandleSed xSedUtenTilhørendeASed kasterException`() {
        val sedHendelse = sedHendelseMedBruker().apply { sedType = "X008" }
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED 555554444 av type X008 har ikke tilhørende A sed behandlet"
    }

    @Test
    fun `behandleSed hvis avsenderId og mottakerId ikke er satt kasterException`() {
        val sedHendelse = sedHendelseUtenAvsenderOgMottakerDetaljer().apply {
            mottakerNavn = "321";
            avsenderNavn = "123";
        }

        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED ${sedHendelse.sedId} mangler avsenderId og mottakerId"
    }

    @Test
    fun `behandleSed hvis avsenderId ikke er satt kasterException`() {
        val sedHendelse = sedHendelseUtenAvsenderOgMottakerDetaljer().apply{
            mottakerId = "123";
            mottakerNavn = "321";
            avsenderNavn = "123";
        }

        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED ${sedHendelse.sedId} mangler avsenderId"
    }

    @Test
    fun `behandleSed hvis mottakerId ikke er satt kasterException`() {
        val sedHendelse = sedHendelseUtenAvsenderOgMottakerDetaljer().apply{
            avsenderId = "123";
            mottakerNavn = "321";
            avsenderNavn = "123";
        }

        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED ${sedHendelse.sedId} mangler mottakerId"
    }

    @Test
    fun `behandleSed hvis avsenderNavn og mottakerNavn ikke er satt kasterException`() {
        val sedHendelse = sedHendelseUtenAvsenderOgMottakerDetaljer().apply {
            avsenderId = "123";
            mottakerId = "321";
        }
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED ${sedHendelse.sedId} mangler avsenderNavn og mottakerNavn"
    }

    @Test
    fun `behandleSed hvis avsenderNavn ikke er satt kasterException`() {
        val sedHendelse = sedHendelseUtenAvsenderOgMottakerDetaljer().apply{
            mottakerNavn = "123";
            avsenderId = "123";
            mottakerId = "321";
        }

        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED ${sedHendelse.sedId} mangler avsenderNavn"
    }

    @Test
    fun `behandleSed hvis mottakerNavn ikke er satt kasterException`() {
        val sedHendelse = sedHendelseUtenAvsenderOgMottakerDetaljer().apply{
            avsenderNavn = "123";
            avsenderId = "123";
            mottakerId = "321";
        }
        val sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build()
        every { journalpostSedKoblingService.erASedAlleredeBehandlet(any()) } returns false
        every { personIdentifisering.identifiserPerson(any(), any()) } returns Optional.of(IDENT)
        every { euxService.hentSedMedRetry(any(), any()) } returns opprettSED()
        every { saksrelasjonService.finnVedRinaSaksnummer(any()).isPresent } returns true;
        every { sedMottattHendelseRepository.save(any<SedMottattHendelse>()) } returnsArgument 0

        shouldThrow<IllegalStateException> {
            sedMottakService.behandleSedMottakHendelse(sedMottattHendelse)
        }.message shouldBe "Mottatt SED ${sedHendelse.sedId} mangler mottakerNavn"
    }

    private fun opprettSED() = SED().apply {
        nav = Nav().apply {
            bruker = Bruker().apply {
                person = Person().apply {
                    statsborgerskap = listOf("NO", "SE").map {
                        Statsborgerskap().apply { land = it }
                    }
                    foedselsdato = "1990-01-01"
                }
            }
        }
        sedType = "A009"
        medlemskap = MedlemskapA009().apply {
            vedtak = VedtakA009().apply {
                gjelderperiode = Periode().apply {
                    fastperiode = Fastperiode().apply {
                        startdato = "2019-05-01"
                        sluttdato = "2019-12-01"
                    }
                }
            }
        }
    }

    private fun sedHendelseMedBruker() = sedHendelseUtenBruker().apply {
        avsenderId = "SE:12345"
        mottakerNavn = "ukjent"
        avsenderNavn = "ukjent"
        navBruker = IDENT
        sedId = SED_ID
        rinaSakId = RINA_SAKSNUMMER
        bucType = BucType.LA_BUC_02.name
    }

    private fun sedHendelseUtenBruker() = SedHendelse().apply {
        navBruker = "ukjent"
        avsenderId = "SE:12345"
        mottakerId = "SE:12345"
        mottakerNavn = "ukjent"
        avsenderNavn = "ukjent"
        rinaSakId = RINA_SAKSNUMMER
        rinaDokumentId = "456"
        sedId = SED_ID
        sedType = "A009"
        bucType = BucType.LA_BUC_02.name
        sektorKode = "LA"
    }

    private fun sedHendelseUtenAvsenderOgMottakerDetaljer() = SedHendelse().apply {
        id = 0;
        sedId = "10977943_389501f50fba4af7a4228fa41b8ee71d_1";
        sektorKode = "LA";
        bucType = "LA_BUC_02";
        rinaSakId = "10977943";
        avsenderId = null;
        avsenderNavn = null;
        mottakerId = null;
        mottakerNavn = null;
        rinaDokumentId = "389501f50fba4af7a4228fa41b8ee71d";
        rinaDokumentVersjon = "1";
        sedType = "X005";
    }


    companion object {
        private const val IDENT = "1122334455"
        private const val SED_ID = "555554444"
        private const val RINA_SAKSNUMMER = "12313213"
    }
}
