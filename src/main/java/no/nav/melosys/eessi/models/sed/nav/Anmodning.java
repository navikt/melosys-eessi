package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Anmodning {

    private String erendring; // Ja/Nei
}
