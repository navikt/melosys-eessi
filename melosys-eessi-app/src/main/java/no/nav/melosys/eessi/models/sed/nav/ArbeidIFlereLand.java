package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ArbeidIFlereLand {

    private Bosted bosted;

    private Yrkesaktivitet yrkesaktivitet;
}
