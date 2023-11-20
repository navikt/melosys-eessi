package no.nav.melosys.eessi.service.joark;

import io.getunleash.Unleash;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.*;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class JournalpostService {

    private final DokkatService dokkatService;
    private final JournalpostMetadataService journalpostMetadataService;
    private final Unleash unleash;
    private final JournalpostapiConsumer journalpostapiConsumer;

    public JournalpostService(DokkatService dokkatService, JournalpostMetadataService journalpostMetadataService, Unleash unleash, JournalpostapiConsumer journalpostapiConsumer) {
        this.dokkatService = dokkatService;
        this.journalpostMetadataService = journalpostMetadataService;
        this.unleash = unleash;
        this.journalpostapiConsumer = journalpostapiConsumer;
    }

    OpprettJournalpostResponse opprettInngaaendeJournalpost(SedHendelse sedHendelse, Sak sak,
                                                            SedMedVedlegg sedMedVedlegg, String personIdent) {
        DokkatSedInfo dokkatSedInfo = hentDokkatSedInfo(sedHendelse.getSedType());
        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse, sedMedVedlegg, sak, finnDokumentTittel(dokkatSedInfo, sedHendelse.getSedType()), finnBehandlingstema(dokkatSedInfo, sedHendelse.getSedType()), personIdent);
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
            sedHendelse, sedMedVedlegg, sak, finnDokumentTittel(dokkatSedInfo, sedHendelse.getSedType()), finnBehandlingstema(dokkatSedInfo, sedHendelse.getSedType()), personIdent);
        return opprettJournalpost(request, true);
    }

    private OpprettJournalpostResponse opprettJournalpost(OpprettJournalpostRequest request, boolean forsokEndeligJfr) {
        return journalpostapiConsumer.opprettJournalpost(request, forsokEndeligJfr);
    }

    private DokkatSedInfo hentDokkatSedInfo(String sedType) {
        return dokkatService.hentMetadataFraDokkat(sedType);
    }

    private String finnDokumentTittel(DokkatSedInfo dokkatSedInfo, String sedType) {
        String dokumentTittel = journalpostMetadataService.hentJournalpostMetadata(sedType).dokumentTittel();
        if (!Objects.equals(dokumentTittel, dokkatSedInfo.getDokumentTittel())) {
            log.error("DokumentTittel fra journalpostMetadataService er ikke lik den vi får fra dokkat: {} vs {}", dokumentTittel, dokkatSedInfo.getDokumentTittel());
        }
        if (unleash.isEnabled("blah")) {
            return dokumentTittel;
        }
        return dokkatSedInfo.getDokumentTittel();
    }

    private String finnBehandlingstema(DokkatSedInfo dokkatSedInfo, String sedType) {
        String behandlingstema = journalpostMetadataService.hentJournalpostMetadata(sedType).behandlingstema();
        if (!Objects.equals(behandlingstema, dokkatSedInfo.getBehandlingstema())) {
            log.error("Behandlingstema fra journalpostMetadataService er ikke lik den vi får fra dokkat: {} vs {}", behandlingstema, dokkatSedInfo.getBehandlingstema());
        }
        if (unleash.isEnabled("blah")) {
            return behandlingstema;
        }
        return dokkatSedInfo.getBehandlingstema();
    }
}
