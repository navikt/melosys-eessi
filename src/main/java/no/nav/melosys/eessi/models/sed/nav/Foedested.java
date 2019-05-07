
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


@JsonInclude(Include.NON_NULL)
@Data
public class Foedested {

    private String by;

    private String land;

    private String region;
}
