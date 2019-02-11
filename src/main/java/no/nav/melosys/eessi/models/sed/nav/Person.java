
package no.nav.melosys.eessi.models.sed.nav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;


@SuppressWarnings("unused")
@JsonInclude(Include.NON_NULL)
@Data
public class Person {

    private String etternavn;

    private String etternavnvedfoedsel;

    private Foedested foedested;

    private String foedselsdato;

    private String fornavn;

    private String fornavnvedfoedsel;

    private String kjoenn;

    private List<Pin> pin;

    private List<Statsborgerskap> statsborgerskap;
}
