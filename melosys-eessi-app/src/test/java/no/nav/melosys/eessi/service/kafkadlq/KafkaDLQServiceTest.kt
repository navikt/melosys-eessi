package no.nav.melosys.eessi.service.kafkadlq

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.melosys.eessi.config.observability.Traceparent
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ
import no.nav.melosys.eessi.models.kafkadlq.QueueType
import no.nav.melosys.eessi.models.kafkadlq.SedSendtHendelseKafkaDLQ
import no.nav.melosys.eessi.repository.KafkaDLQRepository
import no.nav.melosys.eessi.service.journalfoering.OpprettUtgaaendeJournalpostService
import no.nav.melosys.eessi.service.mottak.SedMottakService
import no.nav.melosys.eessi.service.oppgave.OppgaveEndretService
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

internal class KafkaDLQServiceTest {

    private val kafkaDLQRepository = mockk<KafkaDLQRepository>()
    private val sedMottakService = mockk<SedMottakService>()
    private val opprettUtgaaendeJournalpostService = mockk<OpprettUtgaaendeJournalpostService>()
    private val oppgaveEndretService = mockk<OppgaveEndretService>()

    private val kafkaDLQService = KafkaDLQService(
        kafkaDLQRepository,
        sedMottakService,
        opprettUtgaaendeJournalpostService,
        oppgaveEndretService
    )

    @Test
    fun `slettKafkaMelding sletter melding når den finnes`() {
        val uuid = UUID.randomUUID()
        val melding = mockk<KafkaDLQ> {
            every { queueType } returns QueueType.SED_MOTTATT_HENDELSE
        }
        every { kafkaDLQRepository.findById(uuid) } returns Optional.of(melding)
        every { kafkaDLQRepository.delete(melding) } just Runs

        kafkaDLQService.slettKafkaMelding(uuid)

        verify(exactly = 1) { kafkaDLQRepository.findById(uuid) }
        verify(exactly = 1) { kafkaDLQRepository.delete(melding) }
    }

    @Test
    fun `slettKafkaMelding kaster NotFoundException når melding ikke finnes`() {
        val uuid = UUID.randomUUID()
        every { kafkaDLQRepository.findById(uuid) } returns Optional.empty()

        val exception = shouldThrow<NotFoundException> {
            kafkaDLQService.slettKafkaMelding(uuid)
        }
        exception.message shouldContain uuid.toString()

        verify(exactly = 0) { kafkaDLQRepository.delete(any()) }
    }

    @Test
    fun `lagring på DLQ lagrer gjeldende traceparent`() {
        val lagret = slot<KafkaDLQ>()
        every { kafkaDLQRepository.save(capture(lagret)) } answers { lagret.captured }

        Traceparent.aktiver(TRACEPARENT).use {
            kafkaDLQService.lagreSedSendtHendelse(SedHendelse.builder().sedId("sed-1").build(), "feil")
        }

        lagret.captured.traceparent shouldBe TRACEPARENT
    }

    @Test
    fun `lagring på DLQ uten aktiv trace lagrer ingen traceparent`() {
        val lagret = slot<KafkaDLQ>()
        every { kafkaDLQRepository.save(capture(lagret)) } answers { lagret.captured }

        kafkaDLQService.lagreSedSendtHendelse(SedHendelse.builder().sedId("sed-1").build(), "feil")

        lagret.captured.traceparent.shouldBeNull()
    }

    @Test
    fun `rekjøring aktiverer lagret traceparent og gjenoppretter konteksten etterpå`() {
        val uuid = UUID.randomUUID()
        val melding = SedSendtHendelseKafkaDLQ().apply {
            id = uuid
            sedSendtHendelse = SedHendelse.builder().sedId("sed-1").build()
            traceparent = TRACEPARENT
        }
        var traceparentUnderRekjøring: String? = null
        every { kafkaDLQRepository.findById(uuid) } returns Optional.of(melding)
        every { kafkaDLQRepository.delete(melding) } just Runs
        every { opprettUtgaaendeJournalpostService.behandleSedSendtHendelse(any()) } answers {
            traceparentUnderRekjøring = Traceparent.nåværende()
        }

        kafkaDLQService.rekjørKafkaMelding(uuid)

        traceparentUnderRekjøring shouldBe TRACEPARENT
        Traceparent.nåværende().shouldBeNull()
    }

    companion object {
        private const val TRACEPARENT = "00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01"
    }
}
