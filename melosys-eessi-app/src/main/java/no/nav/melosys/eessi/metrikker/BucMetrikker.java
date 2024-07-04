package no.nav.melosys.eessi.metrikker;

import java.util.Arrays;

import io.micrometer.core.instrument.Metrics;
import no.nav.melosys.eessi.models.BucType;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.*;

@Component
public class BucMetrikker {

    static {
        Arrays.stream(BucType.values()).forEach(BucMetrikker::initialiserBucTypeMetrikker);
    }

    private static void initialiserBucTypeMetrikker(BucType bucType) {
        Metrics.counter(BUC_OPPRETTET, KEY_BUCTYPE, bucType.name());
        Metrics.counter(BUC_LUKKET, KEY_BUCTYPE, bucType.name());
    }

    public void bucOpprettet(String bucType) {
        Metrics.counter(BUC_OPPRETTET, KEY_BUCTYPE, bucType).increment();
    }

    public void bucLukket(String bucType) {
        Metrics.counter(BUC_LUKKET, KEY_BUCTYPE, bucType).increment();
    }
}
