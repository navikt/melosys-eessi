package no.nav.melosys.eessi.service.journalfoering

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.benas.randombeans.api.EnhancedRandom
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.melosys.eessi.EnhancedRandomCreator
import no.nav.melosys.eessi.integration.journalpostapi.*
import no.nav.melosys.eessi.integration.sak.Sak
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.nio.charset.Charset

class JournalpostServiceTest {
    private val journalpostapiConsumer: JournalpostapiConsumer = mockk()
    private val journalpostMetadataService: JournalpostMetadataService = mockk()
    private val sedMetrikker: SedMetrikker = mockk()

    private val random: EnhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom()
    private val journalpostMetadata = JournalpostMetadata("dokumentTittel fra journalpostMetadata", "behandlingstema fra journalpostMetadata")

    private lateinit var journalpostService: JournalpostService

    private var sedHendelse: SedHendelse  = random.nextObject(SedHendelse::class.java)
    private var sak: Sak= random.nextObject(Sak::class.java)
    private var objectMapper: ObjectMapper = ObjectMapper()

    private val JOURNALPOST_RESPONSE = "{\"journalpostId\":\"498371665\",\"journalstatus\":\"J\",\"melding\":null,\"dokumenter\":[{\"dokumentInfoId\":\"520426094\"}]}"

    @BeforeEach
    fun setUp() {
        journalpostService = JournalpostService(journalpostMetadataService, journalpostapiConsumer, sedMetrikker)
        every { journalpostMetadataService.hentJournalpostMetadata(any()) } returns journalpostMetadata
    }

    @Test
    fun opprettInngaaendeJournalpost_verifiserEndeligJfr() {
        every { journalpostapiConsumer.opprettJournalpost(any(), eq(false)) } returns OpprettJournalpostResponse.builder().build()

        journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(ByteArray(0)), "123321")

        verify { journalpostapiConsumer.opprettJournalpost(any<OpprettJournalpostRequest>(), eq(false)) }
    }

    @Test
    fun opprettUtgaaendeJournalpost_verifiserEndeligJfr() {
        every { journalpostapiConsumer.opprettJournalpost(any(), eq(true)) } returns OpprettJournalpostResponse.builder().build()

        journalpostService.opprettUtgaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(ByteArray(0)), "123321")
        verify { journalpostapiConsumer.opprettJournalpost(any<OpprettJournalpostRequest>(), eq(true)) }
    }

    @Test
    fun opprettInngaaendeJournalpost_verifiserDokumentTittelOgBehandlingstema() {
        every { journalpostapiConsumer.opprettJournalpost(any(), eq(false)) } returns OpprettJournalpostResponse.builder().build()

        journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(ByteArray(0)), "123321")
        val captor = slot<OpprettJournalpostRequest>()
        verify { journalpostapiConsumer.opprettJournalpost(capture(captor), eq(false)) }
        captor.captured.let {
            it shouldNotBe null
            it.tittel shouldBe journalpostMetadata.dokumentTittel
            it.behandlingstema shouldBe journalpostMetadata.behandlingstema
        }
    }

    @Test
    fun opprettUtgaaendeJournalpost_verifiserDokumentTittelOgBehandlingstema() {
        every { journalpostapiConsumer.opprettJournalpost(any(), eq(true)) } returns OpprettJournalpostResponse.builder().build()

        journalpostService.opprettUtgaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(ByteArray(0)), "123321")
        val captor = slot<OpprettJournalpostRequest>()

        verify { journalpostapiConsumer.opprettJournalpost(capture(captor), eq(true)) }
        captor.captured.let {
            it shouldNotBe null
            it.tittel shouldBe journalpostMetadata.dokumentTittel
            it.behandlingstema shouldBe journalpostMetadata.behandlingstema
        }
    }

    @Test
    fun opprettInngaaendeJournalpos_sedAlleredeJournalførtException_returnererOpprettJournalpostResponse() {
        val httpClientErrorException = HttpClientErrorException(HttpStatus.CONFLICT, "", JOURNALPOST_RESPONSE.toByteArray(), Charset.defaultCharset())
        every {
            journalpostapiConsumer.opprettJournalpost(any(), eq(false))
        } throws SedAlleredeJournalførtException("Sed allerede journalført", "123", httpClientErrorException)
        every {
            journalpostapiConsumer.henterJournalpostResponseFra409Exception(httpClientErrorException)
        } returns objectMapper.readValue(JOURNALPOST_RESPONSE, OpprettJournalpostResponse::class.java)

        val opprettJournalpostResponse = journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(ByteArray(0)), "123321")

        opprettJournalpostResponse.journalpostId shouldBe "498371665"
        verify { journalpostapiConsumer.opprettJournalpost(any<OpprettJournalpostRequest>(), eq(false)) }
    }

    private fun sedMedVedlegg(innhold: ByteArray): SedMedVedlegg {
        return SedMedVedlegg(SedMedVedlegg.BinaerFil("", "", innhold), emptyList())
    }
}
