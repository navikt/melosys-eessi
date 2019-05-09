package no.nav.melosys.eessi.controller.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedDataDto {

    //Søknaddok.
    private List<Ident> utenlandskIdent;

    //Persondok.
    private List<FamilieMedlem> familieMedlem;
    private Bruker bruker;

    //Andre medlemsvariabler
    private Adresse bostedsadresse;
    private List<Virksomhet> arbeidsgivendeVirksomheter;
    private List<Virksomhet> selvstendigeVirksomheter;
    private List<Arbeidssted> arbeidssteder;
    private List<Virksomhet> utenlandskeVirksomheter;

    //Lovvalg
    private List<Lovvalgsperiode> lovvalgsperioder;
    private List<Lovvalgsperiode> tidligereLovvalgsperioder;

    private Long gsakSaksnummer;

    private String mottakerLand;
}
