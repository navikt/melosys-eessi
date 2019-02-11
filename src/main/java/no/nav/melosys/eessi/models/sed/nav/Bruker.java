
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Bruker {

    private List<Adresse> adresse;

    private Far far;

    private Mor mor;

    private Person person;
}
