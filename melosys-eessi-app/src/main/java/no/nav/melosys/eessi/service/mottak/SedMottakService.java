package no.nav.melosys.eessi.service.mottak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SedMottakService {

    private final EuxService euxService;
    private final PersonIdentifisering personIdentifisering;
    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final OppgaveService oppgaveService;
    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final BucIdentifisertService bucIdentifisertService;


    public void behandleSed(SedMottattHendelse sedMottattHendelse) {
        var lagretHendelse = sedMottattHendelseRepository.save(sedMottattHendelse);

        final var sed = euxService.hentSedMedRetry(sedMottattHendelse.getSedHendelse().getRinaSakId(),
                sedMottattHendelse.getSedHendelse().getRinaDokumentId());

        try {
            lagretHendelse.setJournalpostId(opprettJournalpost(lagretHendelse));
        } catch (SedAlleredeJournalførtException e) {
            log.info("Inngående SED {} allerede journalført", e.getSedID());
            sedMottattHendelseRepository.delete(lagretHendelse);
            return;
        }

        //Håndterer aldri X100 SEDer
        if (sed.erX100SED()) {
            return;
        }

        sedMottattHendelseRepository.save(lagretHendelse);
        log.info("Søker etter person for SED");
        personIdentifisering.identifiserPerson(lagretHendelse.getSedHendelse().getRinaSakId(), sed)
                .ifPresentOrElse(
                    ident -> bucIdentifisertService.lagreIdentifisertPerson(lagretHendelse.getSedHendelse().getRinaSakId(), ident),
                    () -> opprettOppgaveIdentifisering(lagretHendelse)
                );
    }

    private String opprettJournalpost(SedMottattHendelse sedMottattHendelse) {
        log.info("Oppretter journalpost for SED {}", sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        var sedMedVedlegg = euxService.hentSedMedVedlegg(
                sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId()
        );

        return opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
                sedMottattHendelse.getSedHendelse(), sedMedVedlegg, null);
    }

    private void opprettOppgaveIdentifisering(SedMottattHendelse sedMottatt) {
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());

        final var rinaSaksnummer = sedMottatt.getSedHendelse().getRinaSakId();
        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer)
                .stream()
                .filter(this::oppgaveErÅpen)
                .findFirst()
                .ifPresentOrElse(
                        b -> log.info("Identifiseringsoppgave {} finnes allerede for rinasak {}", b.getOppgaveId(), rinaSaksnummer),
                        () -> opprettOgLagreIdentifiseringsoppgave(sedMottatt)
                );
    }

    private boolean oppgaveErÅpen(BucIdentifiseringOppg bucIdentifiseringOppg) {
        return oppgaveService.hentOppgave(bucIdentifiseringOppg.getOppgaveId()).erÅpen();
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
