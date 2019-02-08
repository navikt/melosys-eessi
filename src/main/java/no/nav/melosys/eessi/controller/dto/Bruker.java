package no.nav.melosys.eessi.controller.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Bruker {
  private String fornavn;
  private String etternavn;
  private LocalDate foedseldato;
  private String kjoenn;
  private String statsborgerskap;
  private String fnr;

}
