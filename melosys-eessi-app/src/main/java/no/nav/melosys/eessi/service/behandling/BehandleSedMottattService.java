package no.nav.melosys.eessi.service.behandling;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.SedKontekst;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.behandling.event.PersonIdentifisertForBucEvent;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.identifisering.PersonIdentifiseringService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.joark.event.OpprettJournalpostEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BehandleSedMottattService {

    private final EuxService euxService;
    private final PersonIdentifiseringService personIdentifiseringService;
    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final OppgaveService oppgaveService;
    private final ApplicationEventPublisher applicationEventPublisher;


    public void behandleSed(SedMottatt sedMottatt) {
        SedKontekst kontekst = sedMottatt.getSedKontekst();
        SED sed = euxService.hentSed(sedMottatt.getSedHendelse().getRinaSakId(),
                sedMottatt.getSedHendelse().getRinaDokumentId());

        // TODO: Kast et checked exceptions om duplikat (409)
        opprettJournalpost(sedMottatt);

        if (!kontekst.isForsoktIdentifisert()) {
            if (!kontekst.isBucForsoktIdentifisert()) {
                // TODO:
                identifiserPerson(sedMottatt, sed);
            }
        }

        if (kontekst.personErIdentifisert()) {
            if (!kontekst.bucErIdentifisert()) {
                applicationEventPublisher.publishEvent(new PersonIdentifisertForBucEvent(kontekst.getBucId(), kontekst.getNavIdent()));
            }
        } else {
            lagreSomIkkeFerdigbehandlet(sedMottatt);
            opprettOppgaveIdentifisering(sedMottatt);
        }
    }

    private void opprettJournalpost(SedMottatt sedMottatt)  {
        log.info("Oppretter journalpost for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        SedMedVedlegg sedMedVedlegg = euxService.hentSedMedVedlegg(
                sedMottatt.getSedHendelse().getRinaSakId(), sedMottatt.getSedHendelse().getRinaDokumentId()
        );

        if (sedMottatt.getSedKontekst().personErIdentifisert()) {
            SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(
                    sedMottatt.getSedHendelse(), sedMedVedlegg, sedMottatt.getSedKontekst().getNavIdent());
            sedMottatt.getSedKontekst().setJournalpostID(sakInformasjon.getJournalpostId());
            sedMottatt.getSedKontekst().setDokumentID(sakInformasjon.getDokumentId());
        } else {
            String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                    sedMottatt.getSedHendelse(), sedMedVedlegg, null);
            sedMottatt.getSedKontekst().setJournalpostID(journalpostID);
        }
    }

    private void identifiserPerson(SedMottatt sedMottatt, SED sed)  {
        log.info("Søker etter person for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        personIdentifiseringService.identifiserPerson(sedMottatt.getSedHendelse().getRinaSakId(), sed)
                .ifPresent(s -> sedMottatt.getSedKontekst().setNavIdent(s));
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setBucForsoktIdentifisert(true);
        sedMottatt.getSedHendelse().setNavBruker(sedMottatt.getSedKontekst().getNavIdent());
    }

    private void lagreSomIkkeFerdigbehandlet(SedMottatt sedMottatt) {

    }

    private void opprettOppgaveIdentifisering(SedMottatt sedMottatt)  {
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        String oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
                sedMottatt.getSedKontekst().getJournalpostID(), sedMottatt.getSedHendelse().getSedType(), sedMottatt.getSedHendelse().getRinaSakId()
        );
        sedMottatt.getSedKontekst().setOppgaveID(oppgaveID);
    }
}
