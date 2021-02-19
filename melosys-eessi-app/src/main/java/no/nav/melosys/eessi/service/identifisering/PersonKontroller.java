package no.nav.melosys.eessi.service.identifisering;

import java.time.LocalDate;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;

@UtilityClass
class PersonKontroller {

    private static final int LOCAL_DATE_LENGTH = 10;

    static boolean harSammeStatsborgerskap(PersonModell person, SED sed) {
        String tpsStatsborgerskap = person.getStatsborgerskapLandkodeISO2();
        Stream<String> sedStatsborgerskap = sed.getNav().getBruker().getPerson().getStatsborgerskap()
                .stream().map(Statsborgerskap::getLand);

        return sedStatsborgerskap.anyMatch(tpsStatsborgerskap::equalsIgnoreCase);
    }

    static boolean harSammeFoedselsdato(PersonModell person, SED sed) {
        final LocalDate registerFødselsdato = person.getFødselsdato();
        return sed.finnPerson().map(no.nav.melosys.eessi.models.sed.nav.Person::getFoedselsdato)
                .map(dato -> dato.substring(0, LOCAL_DATE_LENGTH))
                .map(LocalDate::parse)
                .filter(registerFødselsdato::equals)
                .isPresent();
    }
}
