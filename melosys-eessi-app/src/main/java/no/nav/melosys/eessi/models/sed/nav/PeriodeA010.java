package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PeriodeA010 {
    // Responsen mottatt fra eux er ikke lik for periode som i eks A009.
    // Fastperiode er ikke et eget wrapper-objekt, mens Aapenperiode er

    private String startdato;

    private String sluttdato;

    private AapenPeriode aapenperiode;

    public boolean erAapenPeriode() {
        return aapenperiode != null && aapenperiode.getStartdato() != null;
    }
}
