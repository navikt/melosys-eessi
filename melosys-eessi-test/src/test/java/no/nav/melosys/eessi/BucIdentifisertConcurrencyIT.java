package no.nav.melosys.eessi;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.repository.BucIdentifisertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
public class BucIdentifisertConcurrencyIT extends ComponentTestBase {

    @Autowired
    private BucIdentifisertService bucIdentifisertService;

    @Autowired
    private BucIdentifisertRepository bucIdentifisertRepository;

    @Test
    // Tester problem med duplikater som blir opprettet pga att vi kjører 2 pods.
    // Vi simulerer det ved å kjøre 2 tråder som prøver å lagre samme bucIdentifisert
    public void sedIdentifisert_duplicateWrites_forhindres() throws InterruptedException {
        String rinaSaksnummer = Integer.toString(new Random().nextInt(100000));
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> bucIdentifisertService.lagreIdentifisertPerson(rinaSaksnummer, "ident"));
        executor.submit(() -> bucIdentifisertService.lagreIdentifisertPerson(rinaSaksnummer, "ident"));

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));
        assertTrue(bucIdentifisertRepository.findByRinaSaksnummer(rinaSaksnummer).isPresent());
    }
}
