package no.nav.melosys.eessi.service.joark;

import no.nav.melosys.eessi.integration.journalpostapi.*;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
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
        DokkatSedInfo dokkatSedInfo = hentDokkatSedInfo(sedHendelse.getSedType());
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse, sedMedVedlegg, sak, finnDokumentTittel(dokkatSedInfo), finnBehandlingstema(dokkatSedInfo), personIdent);
        try {
            return opprettJournalpost(request, false);
        } catch (SedAlleredeJournalførtException e) {
            return journalpostapiConsumer.henterJournalpostResponseFra409Exception(e.getEx());
        }
    }

    OpprettJournalpostResponse opprettUtgaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
                                                           SedMedVedlegg sedMedVedlegg, String personIdent) {
        DokkatSedInfo dokkatSedInfo = hentDokkatSedInfo(sedHendelse.getSedType());
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettUtgaaendeJournalpost(
            sedHendelse, sedMedVedlegg, sak, finnDokumentTittel(dokkatSedInfo), finnBehandlingstema(dokkatSedInfo), personIdent);
        return opprettJournalpost(request, true);
    }

    private OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean forsokEndeligJfr) {
        return journalpostapiConsumer.opprettJournalpost(request, forsokEndeligJfr);
    }

    private DokkatSedInfo hentDokkatSedInfo(String sedType) {
        return dokkatService.hentMetadataFraDokkat(sedType);
    }

    private String finnDokumentTittel(DokkatSedInfo dokkatSedInfo) {
        return dokkatSedInfo.getDokumentTittel();
    }

    private String finnBehandlingstema(DokkatSedInfo dokkatSedInfo) {
        return dokkatSedInfo.getBehandlingstema();
    }
}
