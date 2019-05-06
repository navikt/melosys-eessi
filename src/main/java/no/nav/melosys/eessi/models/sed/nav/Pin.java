
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pin {

    public Pin(String identifikator, String land, String sektor) {
        this.identifikator = identifikator;
        this.land = land;
        this.sektor = sektor;
    }

    private String identifikator;

    private String land;

    private String sektor;

    private String institusjonsid;

    private String institusjonsnavn;
}
