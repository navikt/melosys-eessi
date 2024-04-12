
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@Data
public class Arbeidsland {

    private List<Arbeidssted> arbeidssted;

    private String harfastarbeidssted;

    private String land;
}
