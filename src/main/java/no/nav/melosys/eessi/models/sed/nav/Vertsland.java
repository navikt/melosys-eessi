package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.Collection;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Vertsland {

    private Collection<Arbeidsgiver> arbeidsgiver;
}
