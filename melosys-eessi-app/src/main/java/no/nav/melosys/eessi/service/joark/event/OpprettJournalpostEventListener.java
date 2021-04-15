package no.nav.melosys.eessi.service.joark.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@AllArgsConstructor
public class OpprettJournalpostEventListener {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final EuxService euxService;

    @TransactionalEventListener
    public void opprettJournalpost(OpprettJournalpostEvent opprettJournalpostEvent) {
        log.info("Oppretter journalpost for SED {}", opprettJournalpostEvent.getSedHendelse().getRinaDokumentId());
        SedMedVedlegg sedMedVedlegg = euxService.hentSedMedVedlegg(
                opprettJournalpostEvent.getSedHendelse().getRinaSakId(), opprettJournalpostEvent.getSedHendelse().getRinaDokumentId()
        );

        if (opprettJournalpostEvent.getSedKontekst().personErIdentifisert()) {
            SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(
                    opprettJournalpostEvent.getSedHendelse(), sedMedVedlegg, opprettJournalpostEvent.getSedKontekst().getNavIdent());
            opprettJournalpostEvent.getSedKontekst().setJournalpostID(sakInformasjon.getJournalpostId());
            opprettJournalpostEvent.getSedKontekst().setDokumentID(sakInformasjon.getDokumentId());
        } else {
            String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                    opprettJournalpostEvent.getSedHendelse(), sedMedVedlegg, null);
            opprettJournalpostEvent.getSedKontekst().setJournalpostID(journalpostID);
        }
    }
}
