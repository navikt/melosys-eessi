package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
public class Grunn {
    private String type;
    private String annet;
}
