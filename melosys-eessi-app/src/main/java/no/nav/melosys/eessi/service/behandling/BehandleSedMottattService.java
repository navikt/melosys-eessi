package no.nav.melosys.eessi.service.behandling;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.SedKontekst;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
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
    private final SedMottattHendelseRepository sedMottattHendelseRepository;


    public void behandleSed(SedMottattHendelse sedMottattHendelse) {
        SED sed = euxService.hentSed(sedMottattHendelse.getSedHendelse().getRinaSakId(),
                sedMottattHendelse.getSedHendelse().getRinaDokumentId());

        // TODO: Kast et checked exceptions om duplikat (409)
        opprettJournalpost(sedMottattHendelse);

        sedMottattHendelseRepository.save(sedMottattHendelse);

        log.info("Søker etter person for SED");
        var ident = personIdentifiseringService.identifiserPerson(sedMottattHendelse.getSedHendelse().getRinaSakId(), sed);

        if (ident.isPresent()) {
            applicationEventPublisher.publishEvent(
                    new PersonIdentifisertForBucEvent(sedMottattHendelse.getSedHendelse().getRinaSakId(), ident.get())
            );
        } else {
            opprettOppgaveIdentifisering(sedMottattHendelse);
        }
    }

    private void opprettJournalpost(SedMottattHendelse sedMottattHendelse)  {
        log.info("Oppretter journalpost for SED {}", sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        SedMedVedlegg sedMedVedlegg = euxService.hentSedMedVedlegg(
                sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId()
        );

        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                sedMottattHendelse.getSedHendelse(), sedMedVedlegg, null);
        sedMottattHendelse.setJournalpostId(journalpostID);
        sedMottattHendelseRepository.save(sedMottattHendelse);
    }

    private void opprettOppgaveIdentifisering(SedMottatt sedMottatt)  {
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());
        String oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
                sedMottatt.getSedKontekst().getJournalpostID(), sedMottatt.getSedHendelse().getSedType(), sedMottatt.getSedHendelse().getRinaSakId()
        );
        sedMottatt.getSedKontekst().setOppgaveID(oppgaveID);
    }
}
