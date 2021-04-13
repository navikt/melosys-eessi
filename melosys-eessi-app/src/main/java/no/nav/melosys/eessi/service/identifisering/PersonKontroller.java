package no.nav.melosys.eessi.service.identifisering;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;

@UtilityClass
class PersonKontroller {

    static boolean harOverlappendeStatsborgerskap(PersonModell person, PersonsokKriterier personsokKriterier) {
        final var søkeKriterierStatsborgerskap = personsokKriterier.getStatsborgerskapISO2();
        return person.getStatsborgerskapLandkodeISO2().stream().anyMatch(søkeKriterierStatsborgerskap::contains);
    }

    static boolean harSammeFoedselsdato(PersonModell personModell, PersonsokKriterier personsokKriterier) {
        return personModell.getFødselsdato().equals(personsokKriterier.getFoedselsdato());
    }
}
