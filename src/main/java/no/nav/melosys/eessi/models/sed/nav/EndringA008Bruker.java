package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EndringA008Bruker {

        private String fornavn;

        private String etternavn;
}
