// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.metrikker;

import java.util.Arrays;

import io.micrometer.core.instrument.Metrics;
import no.nav.melosys.eessi.models.SedType;
import org.springframework.stereotype.Component;

import static no.nav.melosys.eessi.metrikker.MetrikkerNavn.*;

@Component
public class SedMetrikker {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SedMetrikker.class);
    private static final String X_SED = "X-SED";
    private static final String H_SED = "H-SED";

    static {
        Arrays.stream(SedType.values()).filter(sedType -> sedType.name().startsWith("A")).forEach(SedMetrikker::initialiserSedTypeTeller);
        Metrics.counter(SED_MOTTATT, KEY_SEDTYPE, X_SED);
        Metrics.counter(SED_MOTTATT, KEY_SEDTYPE, H_SED);
        Metrics.counter(SED_SENDT, KEY_SEDTYPE, X_SED);
        Metrics.counter(SED_SENDT, KEY_SEDTYPE, H_SED);
    }

    private static void initialiserSedTypeTeller(SedType sedType) {
        Metrics.counter(SED_MOTTATT, KEY_SEDTYPE, sedType.name());
        Metrics.counter(SED_SENDT, KEY_SEDTYPE, sedType.name());
        Metrics.counter(SED_MOTTATT_FEILET, KEY_SEDTYPE, sedType.name());
        Metrics.counter(SED_MOTTATT_ALLEREDEJOURNALFOERT, KEY_SEDTYPE, sedType.name());
    }

    public void sedMottatt(String sedType) {
        sedHendelse(SED_MOTTATT, sedType);
    }

    public void sedMottattFeilet(String sedType) {
        Metrics.counter(SED_MOTTATT_FEILET, KEY_SEDTYPE, sedType).increment();
    }

    public void sedMottattAlleredejournalfoert(String sedType) {
        Metrics.counter(SED_MOTTATT_ALLEREDEJOURNALFOERT, KEY_SEDTYPE, sedType).increment();
    }

    public void sedSendt(String sedType) {
        sedHendelse(SED_SENDT, sedType);
    }

    private void sedHendelse(String key, String sedType) {
        if (sedType.startsWith("A")) {
            Metrics.counter(key, KEY_SEDTYPE, sedType).increment();
        } else if (sedType.startsWith("X")) {
            Metrics.counter(key, KEY_SEDTYPE, X_SED).increment();
        } else if (sedType.startsWith("H")) {
            Metrics.counter(key, KEY_SEDTYPE, H_SED).increment();
        } else {
            log.info("Kan ikke telle ukjent sedtype {}", sedType);
        }
    }

    public void sedPdfKonverteringFeilet() {
        Metrics.counter(SED_KONVERTERING_FEILET).increment();
    }
}
