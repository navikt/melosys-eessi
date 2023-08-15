package no.nav.melosys.eessi.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.service.joark.OpprettUtgaaendeJournalpostService;
import no.nav.melosys.eessi.service.sed.SedService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Protected
@Slf4j
@RestController
@RequestMapping
public class SedController {

    private final SedService sedService;
    private final OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    @Autowired
    public SedController(SedService sedService, OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService) {
        this.sedService = sedService;
        this.opprettUtgaaendeJournalpostService = opprettUtgaaendeJournalpostService;
    }

    @PostMapping("/sed/{sedType}/pdf")
    public byte[] genererPdfFraSed(@RequestBody SedDataDto sedDataDto, @PathVariable SedType sedType) throws ValidationException {
        if (sedType.kreverAdresse() && sedDataDto.manglerAdresser()) {
            throw new ValidationException("Personen mangler adresse ved PDF generering");
        }
        return sedService.genererPdfFraSed(sedDataDto, sedType);
    }

    @GetMapping("/journalfoerTidligereSendteSedFor/{rinaSaksnummer}")
    public ResponseEntity<String> hentSed(@PathVariable String rinaSaksnummer) {
            try {
                opprettUtgaaendeJournalpostService.journalfoerTidligereSedDersomEksisterer(rinaSaksnummer);
                return ResponseEntity.status(200).build();
            } catch (Exception e) {
                return ResponseEntity.status(500).build();
            }
    }
}
