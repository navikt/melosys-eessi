package no.nav.melosys.eessi.service.kontroll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.AdminTjeneste;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Unprotected
@RestController
@RequestMapping("/admin/sedmottak")
public class SedMottakAdminTjeneste implements AdminTjeneste {

    private final Logger log = LoggerFactory.getLogger(SedMottakAdminTjeneste.class);

    private final SedMottakService sedMottakService;
    private final SedMottattHendelseRepository sedMottattHendelseRepository;
    private final String apiKey;

    public SedMottakAdminTjeneste(SedMottakService sedMottakService,
                                  SedMottattHendelseRepository sedMottattHendelseRepository,
                                  @Value("${Melosys-admin.apikey}") String apiKey) {
        this.sedMottakService = sedMottakService;
        this.sedMottattHendelseRepository = sedMottattHendelseRepository;
        this.apiKey = apiKey;
    }

    @GetMapping("/feilede")
    public ResponseEntity<List<SedMottattHendelse>> hentSederMottattUtenJournalpostId(
        @RequestHeader(API_KEY_HEADER) String apiKey) {

        validerApikey(apiKey);
        return ResponseEntity.ok(hentAlleSederUtenJournalpostID());
    }

    @PostMapping("/feilede/restart")
    public ResponseEntity<List<SedMottattHendelse>> restartAlleFeiledeProsessinstanser(
        @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        Collection<SedMottattHendelse> feildeSeder = hentAlleSederUtenJournalpostID();
        log.info("Forsøker å restarte feilede SEDer ");
        restartAlleFeiledeSeder(feildeSeder);
        return ResponseEntity.ok(
            new ArrayList<>(feildeSeder)
        );
    }

    private List<SedMottattHendelse> hentAlleSederUtenJournalpostID() {
        return sedMottattHendelseRepository
            .findAll()
            .stream()
            .filter(sedMottattHendelse -> sedMottattHendelse.getJournalpostId() == null)
            .collect(Collectors.toList());
    }

    private void restartAlleFeiledeSeder(Collection<SedMottattHendelse> sedmottattHendelser) {

        sedmottattHendelser
            .forEach(sedMottakService::behandleSed);
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
}
