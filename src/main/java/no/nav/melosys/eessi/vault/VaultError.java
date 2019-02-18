package no.nav.melosys.eessi.vault;

public final class VaultError extends Exception {
    public VaultError(String message, Throwable cause) {
        super(message, cause);
    }
}