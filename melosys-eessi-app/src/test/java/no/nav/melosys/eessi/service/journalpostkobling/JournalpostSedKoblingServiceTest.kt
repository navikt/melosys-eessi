package no.nav.melosys.eessi.service.journalpostkobling

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.melosys.eessi.controller.dto.SedStatus
import no.nav.melosys.eessi.integration.saf.SafConsumer
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import no.nav.melosys.eessi.models.JournalpostSedKobling
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Creator
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.buc.Organisation
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.Nav
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class JournalpostSedKoblingServiceTest {

    @MockK
    private lateinit var journalpostSedKoblingRepository: JournalpostSedKoblingRepository

    @MockK
    private lateinit var euxService: EuxService

    @MockK
    private lateinit var saksrelasjonService: SaksrelasjonService

    private lateinit var journalpostSedKoblingService: JournalpostSedKoblingService

    @BeforeEach
    fun setup() {
        journalpostSedKoblingService = JournalpostSedKoblingService(
            journalpostSedKoblingRepository, euxService, saksrelasjonService, mockk<SafConsumer>(), MelosysEessiMeldingMapperFactory("dummy")
        )
    }

    @Test
    fun finnVedJournalpostIDOpprettMelosysEessiMelding_sakEksistererIDB_forventMelosysEessiMelding() {
        val fagsakRinasakKobling = mockk<FagsakRinasakKobling>()
        every { fagsakRinasakKobling.gsakSaksnummer } returns 123L

        every { journalpostSedKoblingRepository.findByJournalpostID(any()) } returns Optional.of(
            JournalpostSedKobling(
                "123",
                "321",
                "sedID",
                "1",
                "LA_BUC_03",
                "A008"
            )
        )
        every { euxService.hentBuc(any()) } returns BUC(
            documents = listOf(
                Document(
                    id = "sedID",
                    type = "A008",
                    status = SedStatus.MOTTATT.engelskStatus,
                    creator = Creator(organisation = Organisation("org1", "DK", "mnb"))
                )
            )
        )
        every { euxService.hentSed(any(), any()) } returns SED(nav = Nav())
        every { saksrelasjonService.finnVedRinaSaksnummer(any()) } returns Optional.of(fagsakRinasakKobling)

        val melosysEessiMelding: MelosysEessiMelding? =
            journalpostSedKoblingService.finnVedJournalpostIDOpprettMelosysEessiMelding("123").orElse(null)

        melosysEessiMelding.shouldNotBeNull().run {
            sedType shouldBe "A008"
            avsender.shouldNotBeNull()
                .avsenderID shouldBe "mnb"
        }
    }

    @Test
    fun `harASedEllerHSedForRinaSak returnerer true når A-SED finnes`() {
        every { journalpostSedKoblingRepository.findByRinaSaksnummer("123") } returns mutableListOf(
            JournalpostSedKobling("jp1", "123", "sedId1", "1", "LA_BUC_02", "A009")
        )

        journalpostSedKoblingService.harASedEllerHSedForRinaSak("123") shouldBe true
    }

    @Test
    fun `harASedEllerHSedForRinaSak returnerer true når H-SED finnes`() {
        every { journalpostSedKoblingRepository.findByRinaSaksnummer("123") } returns mutableListOf(
            JournalpostSedKobling("jp1", "123", "sedId1", "1", "H_BUC_01", "H001")
        )

        journalpostSedKoblingService.harASedEllerHSedForRinaSak("123") shouldBe true
    }

    @Test
    fun `harASedEllerHSedForRinaSak returnerer false når kun X-SED finnes`() {
        every { journalpostSedKoblingRepository.findByRinaSaksnummer("123") } returns mutableListOf(
            JournalpostSedKobling("jp1", "123", "sedId1", "1", "LA_BUC_02", "X001")
        )

        journalpostSedKoblingService.harASedEllerHSedForRinaSak("123") shouldBe false
    }

    @Test
    fun `harASedEllerHSedForRinaSak returnerer false når ingen koblinger finnes`() {
        every { journalpostSedKoblingRepository.findByRinaSaksnummer("123") } returns mutableListOf()

        journalpostSedKoblingService.harASedEllerHSedForRinaSak("123") shouldBe false
    }
}
