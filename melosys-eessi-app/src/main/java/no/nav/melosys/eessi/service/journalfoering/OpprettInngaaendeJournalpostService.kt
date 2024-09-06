package no.nav.melosys.eessi.service.journalfoering

import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpprettInngaaendeJournalpostService @Autowired constructor(
    private val journalpostService: JournalpostService,
    private val journalpostSedKoblingService: JournalpostSedKoblingService
) {
    fun arkiverInngaaendeSedUtenBruker(sedHendelse: SedHendelse, sedMedVedlegg: SedMedVedlegg, personIdent: String?): String {
        return opprettJournalpostLagreRelasjon(sedHendelse, sedMedVedlegg, personIdent).journalpostId
    }

    private fun opprettJournalpostLagreRelasjon(
        sedMottatt: SedHendelse,
        sedMedVedlegg: SedMedVedlegg,
        personIdent: String?
    ): OpprettJournalpostResponse {
        val response = journalpostService.opprettInngaaendeJournalpost(sedMottatt, null, sedMedVedlegg, personIdent)
        lagreJournalpostRelasjon(sedMottatt, response)
        return response
    }

    private fun lagreJournalpostRelasjon(sedHendelse: SedHendelse, opprettJournalpostResponse: OpprettJournalpostResponse) {
        journalpostSedKoblingService.lagre(
            opprettJournalpostResponse.journalpostId,
            sedHendelse.rinaSakId,
            sedHendelse.rinaDokumentId,
            sedHendelse.rinaDokumentVersjon,
            sedHendelse.bucType,
            sedHendelse.sedType
        )
    }
}
