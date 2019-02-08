package no.nav.melosys.eessi.controller.dto;

import lombok.Data;

@Data
public class Arbeidssted {

  private String navn;
  private Adresse adresse;
  private boolean fysisk;
  private String hjemmebase;
}
