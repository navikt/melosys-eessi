package no.nav.melosys.eessi.models.sed.nav;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VedtakA003 extends Vedtak {

    private String gjeldervarighetyrkesaktivitet;

    private String erendringsvedtak;

    private PeriodeA010 gjelderperiode;

    private String land;
}
