// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.service.sed.SedJournalføringMigreringRapportDto;
import no.nav.melosys.eessi.service.sed.SedJournalføringMigreringService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Protected
@RestController
@RequestMapping("/admin")
public class SedJournalføringAdminTjeneste {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SedJournalføringAdminTjeneste.class);
    private static final String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";
    private final String apiKey;
    private final SedJournalføringMigreringService sedJournalføringMigreringService;

    public SedJournalføringAdminTjeneste(@Value("${melosys.admin.api-key}") String apiKey, SedJournalføringMigreringService sedJournalføringMigreringService) {
        this.apiKey = apiKey;
        this.sedJournalføringMigreringService = sedJournalføringMigreringService;
    }

    @PostMapping("/sed/sed-med-vedlegg/mottatt/start")
    public ResponseEntity<String> startKartleggingAvSedMottattMedVedlegg(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        sedJournalføringMigreringService.startKartleggingAvSedMottatt();
        return ResponseEntity.ok("Startet rapportering av sed mottatt med vedlegg");
    }

    @PostMapping("/sed/sed-med-vedlegg/sendt/start")
    public ResponseEntity<String> startKartleggingAvSedSendtMedVedlegg(@RequestHeader(API_KEY_HEADER) String apiKey) throws IOException, URISyntaxException {
        validerApikey(apiKey);
        sedJournalføringMigreringService.startKartleggingAvSedSendt();
        return ResponseEntity.ok("Startet rapportering av sed sendt med vedlegg");
    }

    @PostMapping("/sed/sed-med-vedlegg/mottatt/stop")
    public ResponseEntity<String> stoppKartleggingAvSedMottattMedVedlegg(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        sedJournalføringMigreringService.stoppSedMottattKartlegging();
        return ResponseEntity.ok("Stoppet rapportering av sed med vedlegg");
    }

    @PostMapping("/sed/sed-med-vedlegg/sendt/stop")
    public ResponseEntity<String> stoppKartleggingAvSedSendtMedVedlegg(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        sedJournalføringMigreringService.stoppSedMendtKartlegging();
        return ResponseEntity.ok("Stoppet rapportering av sed med vedlegg");
    }

    @GetMapping("/sed/sed-med-vedlegg/status")
    public ResponseEntity<SedJournalføringMigreringRapportDto> hentStatusForKartleggingAvVedleggMedSed(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        return ResponseEntity.ok(sedJournalføringMigreringService.hentStatus());
    }

    private void validerApikey(String value) {
        if (!apiKey.equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }
}
