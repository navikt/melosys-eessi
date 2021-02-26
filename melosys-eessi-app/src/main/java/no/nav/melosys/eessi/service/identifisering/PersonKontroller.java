package no.nav.melosys.eessi.service.identifisering;

import lombok.experimental.UtilityClass;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;

@UtilityClass
class PersonKontroller {

    static boolean harSammeStatsborgerskap(PersonModell person, PersonsoekKriterier personsoekKriterier) {
        final var søkeKriterierStatsborgerskap = personsoekKriterier.getStatsborgerskapISO2();
        return person.getStatsborgerskapLandkodeISO2().stream().anyMatch(søkeKriterierStatsborgerskap::contains);
    }

    static boolean harSammeFoedselsdato(PersonModell personModell, PersonsoekKriterier personsoekKriterier) {
        return personModell.getFødselsdato().equals(personsoekKriterier.getFoedselsdato());
    }
}
