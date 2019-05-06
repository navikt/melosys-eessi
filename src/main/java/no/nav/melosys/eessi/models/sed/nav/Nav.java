
package no.nav.melosys.eessi.models.sed.nav;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Nav {
    private List<Arbeidssted> arbeidssted;

    private Bruker bruker;

    private Selvstendig selvstendig;

    private String ytterligereinformasjon;

    private List<Arbeidsgiver> arbeidsgiver;

    private Sak sak;

    private EessiSak eessisak;
}
