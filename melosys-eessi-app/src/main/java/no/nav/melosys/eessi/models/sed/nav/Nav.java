
package no.nav.melosys.eessi.models.sed.nav;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import no.nav.melosys.eessi.models.sed.ArbeidsgiverDeserializer;


@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Nav {
    private List<Arbeidssted> arbeidssted;

    private Bruker bruker;

    private Selvstendig selvstendig;

    private String ytterligereinformasjon;

    //Kan forekomme som et enkelt objekt ved feks H001
    @JsonDeserialize(using = ArbeidsgiverDeserializer.class)
    private List<Arbeidsgiver> arbeidsgiver;

    //Kun for X001 og X006
    private Sak sak;

    private EessiSak eessisak;
}
