
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class Arbeidssted {

    private Adresse adresse;

    private String erikkefastadresse;

    private String hjemmebase;

    private String navn;
}
