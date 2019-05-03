package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class EndringA008 {

    private String periode;

    private Adresse arbeidssted;

    private Adresse adresse;

    private EndringA008Bruker bruker;

    private String trerikraftfra;

    private String startdato;

    private String sluttdato;
}
