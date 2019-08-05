package no.nav.melosys.eessi.service.joark;

import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequestMapper;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import org.springframework.stereotype.Service;

@Service
public class JournalpostService {

    private final DokkatService dokkatService;
    private final JournalpostapiConsumer journalpostapiConsumer;

    public JournalpostService(DokkatService dokkatService, JournalpostapiConsumer journalpostapiConsumer) {
        this.dokkatService = dokkatService;
        this.journalpostapiConsumer = journalpostapiConsumer;
    }

    OpprettJournalpostResponse opprettInngaaendeJournalpost(SedHendelse sedHendelse, Sak sak, byte[] sedPdf)
            throws IntegrationException {
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
                sedHendelse, sedPdf, sak, dokkatService.hentMetadataFraDokkat(sedHendelse.getSedType()));
        return opprettJournalpost(request, false);
    }

    OpprettJournalpostResponse opprettUtgaaendeJournalpost(SedHendelse sedHendelse, Sak sak, byte[] sedPdf)
            throws IntegrationException {
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettUtgaaendeJournalpost(
                sedHendelse, sedPdf, sak, dokkatService.hentMetadataFraDokkat(sedHendelse.getSedType()));
        return opprettJournalpost(request, true);
    }

    private OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean forsokEndeligJfr) {
        return journalpostapiConsumer.opprettJournalpost(request, forsokEndeligJfr);
    }
}
