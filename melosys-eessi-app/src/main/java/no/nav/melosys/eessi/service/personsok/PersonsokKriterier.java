package no.nav.melosys.eessi.service.personsok;

import java.time.LocalDate;
import java.util.Collection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonsokKriterier {
    private String fornavn;
    private String etternavn;
    private LocalDate foedselsdato;
    private Collection<String> statsborgerskapISO2;
}
