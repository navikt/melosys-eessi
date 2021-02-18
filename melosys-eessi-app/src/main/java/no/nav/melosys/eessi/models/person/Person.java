package no.nav.melosys.eessi.models.person;

import java.time.LocalDate;
import java.util.Collection;

import lombok.Value;

@Value
public class Person {
    String ident;
    String fornavn;
    String etternavn;
    LocalDate fødselsdato;
    Collection<String> statsborgerskapLandkoder;
    //TPS: status UTPE (utgått) eller UTAN (utgått annullert tilgang)
    //PDL: status "opphoert" eller "ikkeBosatt" (sistnevnte svarer også til UREG i TPS)
    boolean erUtgått;
}
