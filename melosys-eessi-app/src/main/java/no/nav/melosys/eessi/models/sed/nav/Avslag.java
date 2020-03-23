package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Avslag {

    private String erbehovformerinformasjon;

    private Land forslagformedlemskap;

    private String begrunnelse;
}
