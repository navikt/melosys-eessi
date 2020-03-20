package no.nav.melosys.eessi.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import no.nav.melosys.eessi.models.SedType;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedGrunnlagDto {
    private SedType sedType;
    private List<Ident> utenlandskIdent;
    private Adresse bostedsadresse;
    private List<Virksomhet> arbeidsgivendeVirksomheter;
    private List<Virksomhet> selvstendigeVirksomheter;
    private List<Arbeidssted> arbeidssteder;
    private List<Lovvalgsperiode> lovvalgsperioder;
    private String ytterligereInformasjon;
}
