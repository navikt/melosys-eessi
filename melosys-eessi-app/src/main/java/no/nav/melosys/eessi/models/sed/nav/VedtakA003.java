package no.nav.melosys.eessi.models.sed.nav;

import lombok.Data;

@Data
public class VedtakA003 {

    private String gjeldervarighetyrkesaktivitet;

    private String erendringsvedtak;

    private String eropprinneligvedtak;

    private String datoforrigevedtak;

    private PeriodeA010 gjelderperiode;

    private String land;
}
