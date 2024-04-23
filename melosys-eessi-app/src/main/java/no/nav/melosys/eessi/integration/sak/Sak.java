package no.nav.melosys.eessi.integration.sak;

import java.time.ZonedDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sak {

  private String id;
  private String tema;
  private String applikasjon;
  private String aktoerId;
  private String orgnr;
  private String fagsakNr;
  private String opprettetAv;
  private ZonedDateTime opprettetTidspunkt;
}
