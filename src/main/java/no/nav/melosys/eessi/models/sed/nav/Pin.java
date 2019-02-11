
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Pin {

    public Pin() {}

    public Pin(String identifikator, String land, String sektor) {
        this.identifikator = identifikator;
        this.land = land;
        this.sektor = sektor;
    }

    private String identifikator;

    private String land;

    private String sektor;
}
