package no.nav.melosys.eessi.service.mottak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
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
        if (sedMottattHendelseRepository.findBySedID(sedMottattHendelse.getSedHendelse().getSedId()).isPresent()) {
            log.info("Mottatt SED {} er allerede behandlet", sedMottattHendelse.getSedHendelse().getSedId());
            return;
        }

        var lagretHendelse = sedMottattHendelseRepository.save(sedMottattHendelse);

        final var sed = euxService.hentSedMedRetry(sedMottattHendelse.getSedHendelse().getRinaSakId(),
            sedMottattHendelse.getSedHendelse().getRinaDokumentId());

        //Håndterer aldri X100 SEDer, bare journalfører mottak
        if (sed.erX100SED()) {
            opprettJournalpost(sedMottattHendelse);
            return;
        }

        log.info("Søker etter person for SED");
        personIdentifisering.identifiserPerson(lagretHendelse.getSedHendelse().getRinaSakId(), sed)
            .ifPresentOrElse(
                ident -> bucIdentifisertService.lagreIdentifisertPerson(lagretHendelse.getSedHendelse().getRinaSakId(), ident),
                () -> opprettOppgaveIdentifisering(lagretHendelse)
            );
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
        String journalpostID = opprettJournalpost(sedMottattHendelse);

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

    private String opprettJournalpost(SedMottattHendelse sedMottattHendelse) {
        log.info("Oppretter journalpost for SED {}", sedMottattHendelse.getSedHendelse().getRinaDokumentId());
        var sedMedVedlegg = euxService.hentSedMedVedlegg(
            sedMottattHendelse.getSedHendelse().getRinaSakId(), sedMottattHendelse.getSedHendelse().getRinaDokumentId()
        );

        String journalpostID = arkiverInngaaendeSed(sedMottattHendelse.getSedHendelse(), sedMedVedlegg);

        sedMottattHendelse.setJournalpostId(journalpostID);
        sedMottattHendelseRepository.save(sedMottattHendelse);
        return journalpostID;
    }

    private String arkiverInngaaendeSed(SedHendelse sedHendelse, SedMedVedlegg sedMedVedlegg) {
        String journalpostID;
        if (sedHendelse.erIkkeX100()) {
            journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(sedHendelse, sedMedVedlegg, null);
        } else {
            // X100 SED behandles ikke videre av melosys. Ferdigstiller derfor med en gang
            journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeX100Sed(sedHendelse, sedMedVedlegg);
        }
        return journalpostID;
    }
}
