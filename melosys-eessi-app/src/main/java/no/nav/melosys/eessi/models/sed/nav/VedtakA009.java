
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


@JsonInclude(Include.NON_NULL)
@Data
public class VedtakA009 {

    private String artikkelforordning;

    private String datoforrigevedtak;

    private String erendringsvedtak;

    private Periode gjelderperiode;

    private String gjeldervarighetyrkesaktivitet;

    private String land;

    private String eropprinneligvedtak;
}
