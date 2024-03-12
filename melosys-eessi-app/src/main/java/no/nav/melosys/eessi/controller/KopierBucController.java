package no.nav.melosys.eessi.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.melosys.eessi.service.buc.KopierBucService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Protected
@RestController
public class KopierBucController {

    private final KopierBucService kopierBucService;

    public KopierBucController(KopierBucService kopierBucService) {
        this.kopierBucService = kopierBucService;
    }

    @ApiResponse(description = "Kopierer over første SED i en BUC til en ny BUC og lagrer saksrelasjon på samme arkivsak som tidligere BUC")
    @PostMapping("/bucer/kopier")
    public Map<String, String> kopierOverBUCer(@RequestBody Collection<String> bucer) {
        var saker = new HashMap<String, String>();
        for (var rinaSaksnummer : bucer) {
            String resultat;
            try {
                resultat = kopierBucService.kopierBUC(rinaSaksnummer);
            } catch (Exception e) {
                resultat = e.getMessage();
            }

            saker.put(rinaSaksnummer, resultat);
        }

        return saker;
    }
}
