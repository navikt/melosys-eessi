package no.nav.melosys.eessi.service.joark;

import no.nav.melosys.eessi.integration.journalpostapi.*;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
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

    OpprettJournalpostResponse opprettInngaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
            SedMedVedlegg sedMedVedlegg, String personIdent) {
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
                sedHendelse, sedMedVedlegg, sak, dokkatService.hentMetadataFraDokkat(sedHendelse.getSedType()), personIdent);
        return opprettJournalpost(request, false);
    }

    OpprettJournalpostResponse opprettUtgaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
            SedMedVedlegg sedMedVedlegg, String personIdent) {
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettUtgaaendeJournalpost(
                sedHendelse, sedMedVedlegg, sak, dokkatService.hentMetadataFraDokkat(sedHendelse.getSedType()), personIdent);
        return opprettJournalpost(request, true);
    }

    private OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean forsokEndeligJfr) {
        return journalpostapiConsumer.opprettJournalpost(request, forsokEndeligJfr);
    }
}
