package no.nav.melosys.eessi.integration.journalpostapi;

import lombok.Getter;
import org.springframework.web.client.HttpClientErrorException;

@Getter
public class SedAlleredeJournalførtException extends RuntimeException {

    private final String sedID;
    private final HttpClientErrorException ex;

    public SedAlleredeJournalførtException(String message, String sedID, HttpClientErrorException ex) {
        super(message);
        this.sedID = sedID;
        this.ex = ex;
    }
}
