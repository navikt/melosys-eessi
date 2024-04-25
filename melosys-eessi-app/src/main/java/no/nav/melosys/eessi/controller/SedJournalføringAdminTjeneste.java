package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.sed.SedJournalføringMigreringRapporteringDto;
import no.nav.melosys.eessi.service.sed.SedJournalføringMigreringService;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Unprotected
@RestController
@RequestMapping("/admin")
public class SedJournalføringAdminTjeneste {

    private final static String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    private final String apiKey;

    private final SedJournalføringMigreringService sedJournalføringMigreringService;

    public SedJournalføringAdminTjeneste(@Value("${melosys.admin.api-key}") String apiKey,
                                         SedJournalføringMigreringService sedJournalføringMigreringService) {
        this.apiKey = apiKey;
        this.sedJournalføringMigreringService = sedJournalføringMigreringService;
    }

    @PostMapping("/sed/sed-med-vedlegg/start")
    public ResponseEntity<String> startRapporteringAvVedleggMedSed(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        sedJournalføringMigreringService.startRapportering();

        return ResponseEntity.ok("Startet rapportering av sed med vedlegg");
    }

    @PostMapping("/sed/sed-med-vedlegg/stop")
    public ResponseEntity<String> stoppRapporteringAvVedleggMedSed(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        sedJournalføringMigreringService.stoppRapportering();
        return ResponseEntity.ok("Stoppet rapportering av sed med vedlegg");
    }

    @GetMapping("/sed/sed-med-vedlegg/status")
    public ResponseEntity<SedJournalføringMigreringRapporteringDto> hentStatusForRapporteringAvVedleggMedSed(@RequestHeader(API_KEY_HEADER) String apiKey) {
        validerApikey(apiKey);
        return ResponseEntity.ok(sedJournalføringMigreringService.hentStatus());
    }

    private void validerApikey(String value) {
        if (!apiKey.equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }
}
