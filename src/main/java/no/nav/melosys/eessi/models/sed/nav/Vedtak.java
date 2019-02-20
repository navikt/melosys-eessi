
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Vedtak {

    private String artikkelforordning;

    private String datoforrigevedtak;

    private String erendringsvedtak;

    private Periode gjelderperiode;

    private String gjeldervarighetyrkesaktivitet;

    private String land;

    private String eropprinneligvedtak;
}
