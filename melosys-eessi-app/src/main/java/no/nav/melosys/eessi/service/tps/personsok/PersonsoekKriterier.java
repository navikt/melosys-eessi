package no.nav.melosys.eessi.service.tps.personsok;

import java.time.LocalDate;
import java.util.Collection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonsoekKriterier {
    private String fornavn;
    private String etternavn;
    private LocalDate foedselsdato;
    private Collection<String> statsborgerskapISO2;
}
