package no.nav.melosys.eessi.controller.dto;

import lombok.Data;

@Data
public class FamilieMedlem {

  private String relasjon; //FARA eller MORA
  private String fornavn;
  private String etternavn;

}
