package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperFactory;
import no.nav.melosys.eessi.models.JournalpostSedKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/journalpost")
public class JournalfoeringKoblingController {

    private final JournalpostSedKoblingService journalpostSedKoblingService;
    private final EuxService euxService;

    public JournalfoeringKoblingController(JournalpostSedKoblingService journalpostSedKoblingService,
            EuxService euxService) {
        this.journalpostSedKoblingService = journalpostSedKoblingService;
        this.euxService = euxService;
    }

    @GetMapping("{journalpostID}/eessimelding")
    public MelosysEessiMelding hentEessiMeldingFraJournalpost(@PathVariable("journalpostID") String journalpostID)
            throws NotFoundException, IntegrationException {

        JournalpostSedKobling journalpostSedKobling = journalpostSedKoblingService.finnVedJournalpostID(journalpostID)
                .orElseThrow(() -> new NotFoundException("Finner ikke eessimelding fra journalpsot " + journalpostID));

        SED sed = euxService.hentSed(journalpostSedKobling.getRinaSaksnummer(), journalpostSedKobling.getSedId());

        return MelosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(journalpostSedKobling.getSedType()))
                    .map(
                        null,
                        sed,
                        journalpostSedKobling.getRinaSaksnummer(),
                        journalpostSedKobling.getSedId(),
                        journalpostSedKobling.getSedType(),
                        journalpostSedKobling.getBucType(),
                        journalpostID,
                        null,
                        null,
                        Integer.valueOf(journalpostSedKobling.getSedVersjon()) != 1
                    );
    }
}
