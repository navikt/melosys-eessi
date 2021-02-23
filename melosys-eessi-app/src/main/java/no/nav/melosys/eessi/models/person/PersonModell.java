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
    //TPS: status UTPE (utgått) eller UTAN (utgått annullert tilgang)
    //PDL: status "opphoert" svarer til UTPE i TPS
    // "ikkeBosatt" svarer til UREG i TPS men har også flere betydninger og vil ikke brukes her
    boolean erOpphørt;
}
