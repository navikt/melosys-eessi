package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.List;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OpprettJournalpostResponse {
  private String journalpostId;
  private List<String> dokumenter;
  private String journalstatus;
  private String melding;
}
