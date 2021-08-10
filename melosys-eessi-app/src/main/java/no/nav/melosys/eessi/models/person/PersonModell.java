package no.nav.melosys.eessi.models.person;

import java.time.LocalDate;
import java.util.Collection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonModell {
    String ident;
    String fornavn;
    String etternavn;
    LocalDate fødselsdato;
    Collection<String> statsborgerskapLandkodeISO2;
    Collection<UtenlandskId> utenlandskId;
    boolean erOpphørt;
}
