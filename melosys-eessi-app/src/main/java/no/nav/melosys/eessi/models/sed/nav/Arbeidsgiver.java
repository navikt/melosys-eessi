
package no.nav.melosys.eessi.models.sed.nav;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


@JsonInclude(Include.NON_NULL)
@Data
public class Arbeidsgiver {

    private Adresse adresse;

    private List<Identifikator> identifikator;

    private String navn;
}
