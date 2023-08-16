package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static no.nav.melosys.eessi.config.MDCOperations.*;

@Slf4j
@Unprotected
@RestController
@RequestMapping("/admin/sedmottatt")
public class SedMottattAdminTjeneste {

    private final SedMottakService sedMottakService;
    private final SedMetrikker sedMetrikker;
    private static final String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    private final String apiKey;

    public SedMottattAdminTjeneste(
        SedMottakService sedMottakService, SedMetrikker sedMetrikker, @Value("${melosys.admin.api-key}") String apiKey) {
        this.sedMottakService = sedMottakService;
        this.sedMetrikker = sedMetrikker;
        this.apiKey = apiKey;
    }

    @PostMapping("")
    public ResponseEntity<String> leggTilSedHendelse(@RequestHeader(API_KEY_HEADER) String apiKey, @RequestBody SedHendelse sedHendelse) {
        validerApikey(apiKey);

        sedMottatt(sedHendelse);

        return ResponseEntity.status(200).build();
    }

    private void sedMottatt(SedHendelse sedHendelse) {
        putToMDC(SED_ID, sedHendelse.getSedId());
        putToMDC(CORRELATION_ID, UUID.randomUUID().toString());

        log.info("Sed mottatt fra SedMottattAdminTjeneste : {}", sedHendelse);

        try {
            sedMottakService.behandleSedMottakHendelse(SedMottattHendelse.builder()
                .sedHendelse(sedHendelse)
                .build());
        } catch (SedAlleredeJournalførtException e) {
            log.warn("SED {} allerede journalført", e.getSedID());
            sedMetrikker.sedMottattAlleredejournalfoert(sedHendelse.getSedType());
        } catch (Exception e) {
            sedMetrikker.sedMottattFeilet(sedHendelse.getSedType());
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            log.error("sedMottatt feilet: {}\n{}", message, sedHendelse, e);
            throw e;
        } finally {
            remove(SED_ID);
            remove(CORRELATION_ID);
        }
    }

    private void validerApikey(String value) {
        if (!apiKey.equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }
}
