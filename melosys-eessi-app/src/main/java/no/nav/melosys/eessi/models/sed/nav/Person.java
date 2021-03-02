
package no.nav.melosys.eessi.models.sed.nav;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;


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

    private Collection<Pin> pin = new HashSet<>();

    private Collection<Statsborgerskap> statsborgerskap = new HashSet<>();

    public Optional<Pin> finnNorskPin() {
        return pin.stream().filter(p -> "NO".equals(p.getLand())).findFirst();
    }
}
