package no.nav.melosys.eessi.service;

public interface AdminTjeneste {
    String API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY";

    String getApiKey();

    default void validerApikey(String value) {
        if (!getApiKey().equals(value)) {
            throw new SecurityException("Trenger gyldig apikey");
        }
    }
}
