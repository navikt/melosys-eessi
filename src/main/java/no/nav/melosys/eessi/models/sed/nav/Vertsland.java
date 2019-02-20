package no.nav.melosys.eessi.models.sed.nav;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Vertsland {

    private List<Arbeidsgiver> arbeidsgiver;
}
