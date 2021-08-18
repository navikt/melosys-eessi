package no.nav.melosys.eessi.service.mottak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.identifisering.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SedMottakService {

    private final EuxService euxService;
    private final PersonIdentifisering personIdentifisering;
    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final OppgaveService oppgaveService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final PersonFasade personFasade;


    public void behandleSed(SedMottattHendelse sedMottattHendelse) {
        sedMottattHendelseRepository.save(sedMottattHendelse);

        var sed = euxService.hentSed(sedMottattHendelse.getSedHendelse().getRinaSakId(),
                sedMottattHendelse.getSedHendelse().getRinaDokumentId());


        try {
            opprettJournalpost(sedMottattHendelse);
        } catch (SedAlleredeJournalførtException e) {
            log.info("Inngående SED {} allerede journalført", e.getSedID());
            return;
        }

        sedMottattHendelseRepository.save(sedMottattHendelse);
        log.info("Søker etter person for SED");
        personIdentifisering.identifiserPerson(sedMottattHendelse.getSedHendelse().getRinaSakId(), sed)
                .ifPresentOrElse(
                        ident -> applicationEventPublisher.publishEvent(
                                new BucIdentifisertEvent(sedMottattHendelse.getSedHendelse().getRinaSakId(), personFasade.hentAktoerId(ident))
                        ),
                        () -> opprettOppgaveIdentifisering(sedMottattHendelse)
                );
    }

    private void opprettJournalpost(SedMottattHendelse sedMottattHendelse) {
        log.info("Oppretter journalpost for SED {}", sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        var sedMedVedlegg = euxService.hentSedMedVedlegg(
                sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId()
        );

        var journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                sedMottattHendelse.getSedHendelse(), sedMedVedlegg, null);
        sedMottattHendelse.setJournalpostId(journalpostID);
    }

    private void opprettOppgaveIdentifisering(SedMottattHendelse sedMottatt) {
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());

        final var rinaSaksnummer = sedMottatt.getSedHendelse().getRinaSakId();
        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer)
                .filter(bucIdentifiseringOppg -> oppgaveService.hentOppgave(bucIdentifiseringOppg.getOppgaveId()).erÅpen())
                .ifPresentOrElse(
                        b -> log.info("Identifiseringsoppgave {} finnes allerede for rinasak {}", b.getOppgaveId(), rinaSaksnummer),
                        () -> opprettOgLagreIdentifiseringsoppgave(sedMottatt)
                );
    }

    private void opprettOgLagreIdentifiseringsoppgave(SedMottattHendelse sedMottattHendelse) {
        var oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
                sedMottattHendelse.getJournalpostId(),
                sedMottattHendelse.getSedHendelse().getSedType(),
                sedMottattHendelse.getSedHendelse().getRinaSakId()
        );
        bucIdentifiseringOppgRepository.save(BucIdentifiseringOppg.builder()
                .rinaSaksnummer(sedMottattHendelse.getSedHendelse().getRinaSakId())
                .oppgaveId(oppgaveID)
                .build());
        log.info("Opprettet oppgave med id {}", oppgaveID);
    }
}
