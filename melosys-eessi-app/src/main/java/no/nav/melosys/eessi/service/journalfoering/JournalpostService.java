package no.nav.melosys.eessi.service.journalfoering;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.*;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JournalpostService {
    private final JournalpostMetadataService journalpostMetadataService;
    private final JournalpostapiConsumer journalpostapiConsumer;
    private final SedMetrikker sedMetrikker;

    public JournalpostService(JournalpostMetadataService journalpostMetadataService,
                              JournalpostapiConsumer journalpostapiConsumer, SedMetrikker sedMetrikker) {
        this.journalpostMetadataService = journalpostMetadataService;
        this.journalpostapiConsumer = journalpostapiConsumer;
        this.sedMetrikker = sedMetrikker;
    }

    OpprettJournalpostResponse opprettInngaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
                                                            SedMedVedlegg sedMedVedlegg, String personIdent) {
        var journalpostMetadata = journalpostMetadataService.hentJournalpostMetadata(sedHendelse.getSedType());
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse, sedMedVedlegg, sak, journalpostMetadata.dokumentTittel(),
            journalpostMetadata.behandlingstema(), personIdent, sedMetrikker);
        try {
            return opprettJournalpost(request, false);
        } catch (SedAlleredeJournalførtException e) {
            return journalpostapiConsumer.henterJournalpostResponseFra409Exception(e.getEx());
        }
    }

    OpprettJournalpostResponse opprettUtgaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
                                                           SedMedVedlegg sedMedVedlegg, String personIdent) {
        var journalpostMetadata = journalpostMetadataService.hentJournalpostMetadata(sedHendelse.getSedType());
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettUtgaaendeJournalpost(
            sedHendelse, sedMedVedlegg, sak, journalpostMetadata.dokumentTittel(),
            journalpostMetadata.behandlingstema(), personIdent, sedMetrikker);
        return opprettJournalpost(request, true);
    }

    private OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean forsokEndeligJfr) {
        return journalpostapiConsumer.opprettJournalpost(request, forsokEndeligJfr);
    }
}
