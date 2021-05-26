
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
@Data
public class VedtakA009 extends Vedtak{

    private String artikkelforordning;

    private String erendringsvedtak;

    private Periode gjelderperiode;

    private String gjeldervarighetyrkesaktivitet;

    private String land;
}
