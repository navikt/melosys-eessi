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
  private List<Dokument> dokumenter;
  private String journalstatus;
  private String melding;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Dokument {
    private String dokumentInfoId;
  }
}
