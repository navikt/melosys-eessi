package no.nav.melosys.eessi.config;

import org.slf4j.MDC;

public final class MDCOperations {

    private MDCOperations() {
    }

    public static final String SED_ID = "sedId";

    public static void putToMDC(String key, String value) {
        MDC.put(key, value);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }
}
