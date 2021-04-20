package no.nav.melosys.eessi.integration.journalpostapi;

import lombok.Getter;

@Getter
public class SedAlleredeJournalførtException extends RuntimeException {

    private final String sedID;

    public SedAlleredeJournalførtException(String message, String sedID) {
        super(message);
        this.sedID = sedID;
    }
}
