package no.nav.melosys.eessi.models.person;

import java.time.LocalDate;

import lombok.Value;

@Value
public class PersonModell {
    String ident;
    String fornavn;
    String etternavn;
    LocalDate fødselsdato;
    String statsborgerskapLandkodeISO2;
    //TPS: status UTPE (utgått) eller UTAN (utgått annullert tilgang)
    //PDL: status "opphoert" eller "ikkeBosatt" (sistnevnte svarer også til UREG i TPS)
    boolean erOpphørt;
}
