package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.JournalpostSedKoblingDto;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Protected
@Slf4j
@RestController
@RequestMapping("/journalpost")
public class JournalfoeringKoblingController {

    private final JournalpostSedKoblingService journalpostSedKoblingService;

    public JournalfoeringKoblingController(JournalpostSedKoblingService journalpostSedKoblingService) {
        this.journalpostSedKoblingService = journalpostSedKoblingService;
    }

    // @ApiOperation(value = "Henter objekt som beskriver dataen mottatt i sed som journalpost er koblet til")
    @GetMapping("{journalpostID}/eessimelding")
    public MelosysEessiMelding hentEessiMeldingFraJournalpost(@PathVariable("journalpostID") String journalpostID) {
        return journalpostSedKoblingService.finnVedJournalpostIDOpprettMelosysEessiMelding(journalpostID)
            .orElseThrow(() -> new NotFoundException("Finner ikke rinasak tilhørende journalpostID " + journalpostID));
    }

    // @ApiOperation(value = "Henter sed koblet til journalpost. Gir tomt svar om det ikke finnes en relasjon")
    @GetMapping("{journalpostID}")
    public JournalpostSedKoblingDto hentJournalpostInfo(@PathVariable("journalpostID") String journalpostID) {
        return journalpostSedKoblingService.finnVedJournalpostID(journalpostID)
            .map(JournalpostSedKoblingDto::new)
            .orElseGet(JournalpostSedKoblingDto::new);
    }
}
