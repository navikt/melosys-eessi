package no.nav.melosys.eessi.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.JournalpostSedKoblingDto;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperFactory;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.JournalpostSedKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
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
    private final SaksrelasjonService saksrelasjonService;

    public JournalfoeringKoblingController(JournalpostSedKoblingService journalpostSedKoblingService,
            EuxService euxService, SaksrelasjonService saksrelasjonService) {
        this.journalpostSedKoblingService = journalpostSedKoblingService;
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    @ApiOperation(value = "Henter objekt som beskriver dataen mottatt i sed som journalpost er koblet til")
    @GetMapping("{journalpostID}/eessimelding")
    public MelosysEessiMelding hentEessiMeldingFraJournalpost(@PathVariable("journalpostID") String journalpostID)
            throws NotFoundException, IntegrationException {

        JournalpostSedKobling journalpostSedKobling = journalpostSedKoblingService.finnVedJournalpostID(journalpostID)
                .orElseThrow(() -> new NotFoundException("Finner ikke eessimelding fra journalpost " + journalpostID));

        SED sed = euxService.hentSed(journalpostSedKobling.getRinaSaksnummer(), journalpostSedKobling.getSedId());

        Long gsakSaksnummer = saksrelasjonService.finnVedRinaId(journalpostSedKobling.getRinaSaksnummer())
                .map(FagsakRinasakKobling::getGsakSaksnummer)
                .orElse(null);

        return MelosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(journalpostSedKobling.getSedType()))
                    .map(
                        null,
                        sed,
                        journalpostSedKobling.getSedId(),
                        journalpostSedKobling.getRinaSaksnummer(),
                        journalpostSedKobling.getSedType(),
                        journalpostSedKobling.getBucType(),
                        journalpostSedKobling.getJournalpostID(),
                        null,
                        gsakSaksnummer != null ? gsakSaksnummer.toString() : null,
                        Integer.parseInt(journalpostSedKobling.getSedVersjon()) != 1
                    );
    }

    @ApiOperation(value = "Henter sed koblet til journalpost. Gir tomt svar om det ikke finnes en relasjon")
    @GetMapping("{journalpostID}")
    public JournalpostSedKoblingDto hentJournalpostInfo(@PathVariable("journalpostID") String journalpostID) {
        return journalpostSedKoblingService.finnVedJournalpostID(journalpostID)
                .map(JournalpostSedKoblingDto::new)
                .orElseGet(JournalpostSedKoblingDto::new);
    }
}
