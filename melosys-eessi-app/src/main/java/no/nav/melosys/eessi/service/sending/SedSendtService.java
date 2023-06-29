package no.nav.melosys.eessi.service.sending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedSendtHendelse;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedSendtHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SedSendtService {

    private final BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    private final OppgaveService oppgaveService;
    private final SedSendtHendelseRepository sedSendtHendelseRepository;

    public void opprettOppgaveIdentifisering(SedHendelse sedSendt) {
        final var rinaSaksnummer = sedSendt.getRinaSakId();
        log.info("Oppretter oppgave til ID og fordeling for SED {}", rinaSaksnummer);


        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer)
            .stream()
            .filter(this::oppgaveErÅpen)
            .findFirst()
            .ifPresentOrElse(
                bucIdentifiseringOppg -> log.info("Identifiseringsoppgave {} finnes allerede for rinasak {}", bucIdentifiseringOppg.getOppgaveId(), rinaSaksnummer),
                () -> opprettOgLagreIdentifiseringsoppgave(sedSendt) //TODO kan nok fjernes siden dette muligens ikke er ett reelt scenario
            );
    }

    private void opprettOgLagreIdentifiseringsoppgave(SedHendelse sedHendelse) {//TODO trenger vi i det hele tatt å gjøre dette?
        var oppgaveID = oppgaveService.opprettOppgaveTilIdOgFordeling( //TODO må skrives om
            null,
            sedHendelse.getSedType(),
            sedHendelse.getRinaSakId()
        );
        bucIdentifiseringOppgRepository.save(BucIdentifiseringOppg.builder()
            .rinaSaksnummer(sedHendelse.getRinaSakId())
            .oppgaveId(oppgaveID)
            .versjon(1)
            .build());

        log.info("Opprettet oppgave med id {}", oppgaveID);
    }

    private boolean oppgaveErÅpen(BucIdentifiseringOppg bucIdentifiseringOppg) {
        return oppgaveService.hentOppgave(bucIdentifiseringOppg.getOppgaveId()).erÅpen();
    }



}
