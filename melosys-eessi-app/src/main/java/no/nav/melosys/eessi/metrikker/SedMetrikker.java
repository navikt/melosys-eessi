package no.nav.melosys.eessi.metrikker;

import java.util.Arrays;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.SedType;
import org.springframework.stereotype.Component;
import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.*;

@Slf4j
@Component
public class SedMetrikker {

    private static final String X_SED = "X-SED";
    private static final String H_SED = "H-SED";

    static {
        Arrays.stream(SedType.values())
                .filter(sedType -> sedType.name().startsWith("A"))
                .forEach(SedMetrikker::initialiserSedTypeTeller);
        Metrics.counter(SED_MOTTATT, KEY_SEDTYPE, X_SED);
        Metrics.counter(SED_MOTTATT, KEY_SEDTYPE, H_SED);
        Metrics.counter(SED_SENDT, KEY_SEDTYPE, X_SED);
        Metrics.counter(SED_SENDT, KEY_SEDTYPE, H_SED);
    }

    private static void initialiserSedTypeTeller(SedType sedType) {
        Metrics.counter(SED_MOTTATT, KEY_SEDTYPE, sedType.name());
        Metrics.counter(SED_SENDT, KEY_SEDTYPE, sedType.name());
    }

    public void sedMottatt(String sedType) {
        sedHendelse(SED_MOTTATT, sedType);
    }

    public void sedSendt(String sedType) {
        sedHendelse(SED_SENDT, sedType);
    }

    private void sedHendelse(String key, String sedType) {
        if (sedType.startsWith("A")) {
            Metrics.counter(key, KEY_SEDTYPE, sedType).increment();
        } else if (sedType.startsWith("X")) {
            Metrics.counter(key, KEY_SEDTYPE, X_SED).increment();
        } else if(sedType.startsWith("H")) {
            Metrics.counter(key, KEY_SEDTYPE, H_SED).increment();
        } else {
            log.info("Kan ikke telle ukjent sedtype {}", sedType);
        }
    }
}
