package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class VedtakA010 extends Vedtak {

    private String gjeldervarighetyrkesaktivitet;

    private PeriodeA010 gjelderperiode;

    private String land;
}
