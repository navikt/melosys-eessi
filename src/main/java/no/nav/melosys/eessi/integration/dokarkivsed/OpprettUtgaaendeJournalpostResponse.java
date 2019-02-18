package no.nav.melosys.eessi.integration.dokarkivsed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpprettUtgaaendeJournalpostResponse {

    @NonNull
    private String kanalreferanseId;
    @NonNull
    private String journalpostId;
    @NonNull
    private JournalTilstand journalfoeringStatus;

    public enum JournalTilstand {
        ENDELIG_JOURNALFOERT,
        MIDLERTIDIG_JOURNALFOERT
    }
}