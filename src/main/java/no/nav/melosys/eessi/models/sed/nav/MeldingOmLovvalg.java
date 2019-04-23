package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MeldingOmLovvalg {
    private String artikkel;
}
