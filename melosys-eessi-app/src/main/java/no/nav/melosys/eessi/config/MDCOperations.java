package no.nav.melosys.eessi.config;

import java.util.UUID;

import org.slf4j.MDC;

public final class MDCOperations {

    private MDCOperations() {
    }

    public static final String SED_ID = "sedId";
    public static final String CORRELATION_ID = "correlation-id";
    public static final String X_CORRELATION_ID = "X-Correlation-ID";

    public static void putToMDC(String key, String value) {
        MDC.put(key, value);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    public static String getCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
