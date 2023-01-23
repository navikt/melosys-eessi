package no.nav.melosys.eessi.service.joark;

import no.finn.unleash.Unleash;
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
    private final Unleash unleash;

    public JournalpostService(DokkatService dokkatService, JournalpostapiConsumer journalpostapiConsumer, Unleash unleash) {
        this.dokkatService = dokkatService;
        this.journalpostapiConsumer = journalpostapiConsumer;
        this.unleash = unleash;
    }

    OpprettJournalpostResponse opprettInngaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
                                                            SedMedVedlegg sedMedVedlegg, String personIdent) {
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse, sedMedVedlegg, sak, dokkatService.hentMetadataFraDokkat(sedHendelse.getSedType()), personIdent);
        try {
            return opprettJournalpost(request, false);
        } catch (SedAlleredeJournalførtException e) {
            if (unleash.isEnabled("melosys.eessi.opprettjournalpost")) {
                return journalpostapiConsumer.henterJournalpostResponseFra409Exception(e.getEx());
            } else {
                throw e;
            }
        }
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
