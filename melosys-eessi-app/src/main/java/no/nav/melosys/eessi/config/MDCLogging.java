package no.nav.melosys.eessi.config;

import org.slf4j.MDC;

public final class MDCLogging {

    private MDCLogging() {}

    private static final String SED_ID = "sedId";

    public static void loggSedID(String sedID) {
        MDC.put(SED_ID, sedID);
    }

    public static void slettSedIDLogging() {
        MDC.remove(SED_ID);
    }
}
