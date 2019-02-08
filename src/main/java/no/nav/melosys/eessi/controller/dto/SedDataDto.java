package no.nav.melosys.eessi.controller.dto;

import java.util.List;
import lombok.Data;

@Data
public class SedDataDto {
  //Søknaddok.
  private Ident utenlandskIdent;

  //Persondok.
  private FamilieMedlem familieMedlem;
  private boolean egenAnsatt;
  private Bruker bruker;

  //Andre medlemsvariabler
  private Adresse bostedsadresse;
  private List<Virksomhet> arbeidsgivendeVirksomheter;
  private List<Virksomhet> selvstendigeVirksomheter;
  private List<Arbeidssted> arbeidssteder;
  private List<Virksomhet> utenlandskeVirksomheter;

  //Lovvalg
  private List<Lovvalgsperiode> lovvalgsperioder;

}
