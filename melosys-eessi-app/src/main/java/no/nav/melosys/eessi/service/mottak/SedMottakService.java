package no.nav.melosys.eessi.service.mottak;

import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.Unleash;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
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
    private final JournalpostSedKoblingService journalpostSedKoblingService;
    private final Unleash unleash;


    @Transactional
    public void behandleSed(SedMottattHendelse sedMottattHendelse) {
        if (sedMottattHendelse.getSedHendelse().erX100()) {
            log.info("Ignorerer mottatt SED {} av typen X100", sedMottattHendelse.getSedHendelse().getSedId());
            return;
        }

        if (sedMottattHendelseRepository.findBySedID(sedMottattHendelse.getSedHendelse().getSedId()).isPresent()) {
            log.info("Mottatt SED {} er allerede behandlet", sedMottattHendelse.getSedHendelse().getSedId());
            return;
        }

        if (unleash.isEnabled("melosys.eessi.sed.rekkefolge") && erXSedBehandletUtenASed(sedMottattHendelse.getSedHendelse())) {
            throw new IllegalStateException(String.format("Mottatt SED %s av type %s har ikke tilhørende A sed behandlet", sedMottattHendelse.getSedHendelse().getSedId(), sedMottattHendelse.getSedHendelse().getSedType()));
        }

        var lagretHendelse = sedMottattHendelseRepository.save(sedMottattHendelse);

        final var sed = euxService.hentSedMedRetry(sedMottattHendelse.getSedHendelse().getRinaSakId(),
            sedMottattHendelse.getSedHendelse().getRinaDokumentId());

        log.info("Søker etter person for SED");
        personIdentifisering.identifiserPerson(lagretHendelse.getSedHendelse().getRinaSakId(), sed)
            .ifPresentOrElse(
                ident -> bucIdentifisertService.lagreIdentifisertPerson(lagretHendelse.getSedHendelse().getRinaSakId(), ident),
                () -> opprettOppgaveIdentifisering(lagretHendelse)
            );
    }

    private boolean erXSedBehandletUtenASed(SedHendelse sedHendelse) {
        return sedHendelse.erXSed() && !journalpostSedKoblingService.erASedAlleredeBehandlet(sedHendelse.getRinaSakId());
    }

    private void opprettOppgaveIdentifisering(SedMottattHendelse sedMottatt) {
        log.info("Oppretter oppgave til ID og fordeling for SED {}", sedMottatt.getSedHendelse().getRinaDokumentId());

        final var rinaSaksnummer = sedMottatt.getSedHendelse().getRinaSakId();
        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer)
            .stream()
            .filter(this::oppgaveErÅpen)
            .findFirst()
            .ifPresentOrElse(
                bucIdentifiseringOppg -> log.info("Identifiseringsoppgave {} finnes allerede for rinasak {}", bucIdentifiseringOppg.getOppgaveId(), rinaSaksnummer),
                () -> opprettOgLagreIdentifiseringsoppgave(sedMottatt)
            );
    }

    private boolean oppgaveErÅpen(BucIdentifiseringOppg bucIdentifiseringOppg) {
        return oppgaveService.hentOppgave(bucIdentifiseringOppg.getOppgaveId()).erÅpen();
    }

    private void opprettOgLagreIdentifiseringsoppgave(SedMottattHendelse sedMottattHendelse) {
        String journalpostID = opprettJournalpost(sedMottattHendelse, null);

        var oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling(
            journalpostID,
            sedMottattHendelse.getSedHendelse().getSedType(),
            sedMottattHendelse.getSedHendelse().getRinaSakId()
        );
        bucIdentifiseringOppgRepository.save(BucIdentifiseringOppg.builder()
            .rinaSaksnummer(sedMottattHendelse.getSedHendelse().getRinaSakId())
            .oppgaveId(oppgaveID)
            .versjon(1)
            .build());

        log.info("Opprettet oppgave med id {}", oppgaveID);
    }

    private String opprettJournalpost(SedMottattHendelse sedMottattHendelse, String navIdent) {
        log.info("Oppretter journalpost for SED {}", sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        var sedMedVedlegg = euxService.hentSedMedVedlegg(
            sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId()
        );

        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
            sedMottattHendelse.getSedHendelse(), sedMedVedlegg, navIdent);

        sedMottattHendelse.setJournalpostId(journalpostID);
        sedMottattHendelseRepository.save(sedMottattHendelse);
        return journalpostID;
    }
}
