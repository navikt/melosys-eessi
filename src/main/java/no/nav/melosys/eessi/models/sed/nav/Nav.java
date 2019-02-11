
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;


@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Nav {
    private List<Arbeidssted> arbeidssted;

    private Bruker bruker;

    private Selvstendig selvstendig;

    private String ytterligereinformasjon;

    private List<Arbeidsgiver> arbeidsgiver;
}
