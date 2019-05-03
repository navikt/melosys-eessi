package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ArbeidIFlereLand {

    private String bostedsland;

    private Yrkesaktivitet yrkesaktivitet;
}
