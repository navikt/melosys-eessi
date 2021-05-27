package no.nav.melosys.eessi.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import no.nav.melosys.eessi.service.buc.KopierBucService;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Protected
@RestController
public class KopierBucController {

    private final KopierBucService kopierBucService;

    public KopierBucController(KopierBucService kopierBucService) {
        this.kopierBucService = kopierBucService;
    }

    @PostMapping("/bucer/kopier")
    public Map<String, String> kopierOverBUCer(Collection<String> bucer) {
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
