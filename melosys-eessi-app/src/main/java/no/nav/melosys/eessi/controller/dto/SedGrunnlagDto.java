package no.nav.melosys.eessi.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedGrunnlagDto {
    private List<Ident> utenlandskIdent;
    private Adresse bostedsadresse;
    private List<Virksomhet> arbeidsgivendeVirksomheter;
    private List<Virksomhet> selvstendigeVirksomheter;
    private List<Arbeidssted> arbeidssteder;
    private List<Lovvalgsperiode> lovvalgsperioder;
    private String ytterligereInformasjon;

    // todo: egen dto? er kun relevant for A003
    private Bestemmelse overgangsregelbestemmelse;
    private List<Virksomhet> norskeArbeidsgivendeVirksomheter;
}
