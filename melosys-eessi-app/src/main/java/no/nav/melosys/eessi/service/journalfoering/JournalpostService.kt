package no.nav.melosys.eessi.service.journalfoering

import no.nav.melosys.eessi.integration.journalpostapi.*
import no.nav.melosys.eessi.integration.sak.Sak
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import org.springframework.stereotype.Service

@Service
class JournalpostService(
    private val journalpostMetadataService: JournalpostMetadataService,
    private val journalpostapiConsumer: JournalpostapiConsumer,
    private val sedMetrikker: SedMetrikker
) {
    fun opprettInngaaendeJournalpost(
        sedHendelse: SedHendelse,
        sak: Sak?,
        sedMedVedlegg: SedMedVedlegg?,
        personIdent: String?
    ): OpprettJournalpostResponse {
        val journalpostMetadata = journalpostMetadataService.hentJournalpostMetadata(sedHendelse.sedType)
        val request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg,
            sak,
            journalpostMetadata.dokumentTittel,
            journalpostMetadata.behandlingstema,
            personIdent,
            sedMetrikker
        )
        return try {
            opprettJournalpost(request, false)
        } catch (e: SedAlleredeJournalførtException) {
            journalpostapiConsumer.henterJournalpostResponseFra409Exception(e.ex)
        }
    }

    fun opprettUtgaaendeJournalpost(
        sedHendelse: SedHendelse,
        sak: Sak?,
        sedMedVedlegg: SedMedVedlegg?,
        personIdent: String?
    ): OpprettJournalpostResponse {
        val journalpostMetadata = journalpostMetadataService.hentJournalpostMetadata(sedHendelse.sedType)
        val request = OpprettJournalpostRequestMapper.opprettUtgaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg,
            sak,
            journalpostMetadata.dokumentTittel,
            journalpostMetadata.behandlingstema,
            personIdent,
            sedMetrikker
        )
        return opprettJournalpost(request, true)
    }

    private fun opprettJournalpost(request: OpprettJournalpostRequest, forsokEndeligJfr: Boolean): OpprettJournalpostResponse =
        journalpostapiConsumer.opprettJournalpost(request, forsokEndeligJfr)
}
