
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Adresse {

    private String by;

    private String bygning;

    private String gate;

    private String land;

    private String postnummer;

    private String region;

    private String type;
}
