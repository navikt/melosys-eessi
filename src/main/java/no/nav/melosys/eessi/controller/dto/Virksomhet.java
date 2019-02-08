package no.nav.melosys.eessi.controller.dto;

import lombok.Data;

@Data
public class Virksomhet {

  private String navn;
  private Adresse adresse;
  private String orgnr;
  private String type; //Trenger kanskje ikke denne?
}
