package no.nav.melosys.eessi.service.kontroll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
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
public class SedMottakAdminTjeneste {

    private final Logger log = LoggerFactory.getLogger(SedMottakAdminTjeneste.class);
    private final static String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

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
    public ResponseEntity<List<SedMottattHendelse>> hentSEDerMottattUtenJournalpostId(
        @RequestHeader(API_KEY_HEADER) String apiKey) {

        validerApikey(apiKey);
        return ResponseEntity.ok(hentAlleSEDerUtenJournalpostID());
    }

    @PostMapping("/feilede/restart")
    public ResponseEntity<List<SedMottattHendelse>> restartAlleSEDerUtenJournalpostId(
        @RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        Collection<SedMottattHendelse> sedUtenJournalpost = hentAlleSEDerUtenJournalpostID();
        log.info("Forsøker å restarte feilede SEDer ");
        restartAlleFeiledeSEDer(sedUtenJournalpost);
        return ResponseEntity.ok(
            new ArrayList<>(sedUtenJournalpost)
        );
    }

    private List<SedMottattHendelse> hentAlleSEDerUtenJournalpostID() {
        return new ArrayList<>(sedMottattHendelseRepository
            .findAllByJournalPostId(null));
    }

    private void restartAlleFeiledeSEDer(Collection<SedMottattHendelse> sedmottattHendelser) {
        sedmottattHendelser
            .forEach(sedMottakService::behandleSed);
    }

    private void validerApikey(String value) {
        if (!getApiKey().equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }

    private String getApiKey() { return apiKey; }
}
