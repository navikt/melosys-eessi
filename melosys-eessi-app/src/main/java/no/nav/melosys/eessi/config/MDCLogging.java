package no.nav.melosys.eessi.config;

import org.slf4j.MDC;

import java.util.UUID;

public final class MDCLogging {

    private MDCLogging() {
    }

    private static final String SED_ID = "sedId";
    public static final String CORRELATION_ID = "correlation-id";
    public static final String X_CORRELATION_ID = "X-Correlation-ID";

    public static void loggSedID(String sedID) {
        MDC.put(SED_ID, sedID);
    }

    public static void slettSedIDLogging() {
        MDC.remove(SED_ID);
    }

    public static void loggCorrelationId() {
        MDC.put(CORRELATION_ID, UUID.randomUUID().toString());
    }

    public static void slettCorrelationId() {
        MDC.remove(CORRELATION_ID);
    }

    public static String getCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
