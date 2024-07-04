// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.identifisering;

import java.time.LocalDate;
import java.util.Collection;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.person.UtenlandskId;
import no.nav.melosys.eessi.models.sed.nav.Kjønn;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;

final class PersonKontroller {
    static boolean harOverlappendeStatsborgerskap(PersonModell person, PersonsokKriterier personsokKriterier) {
        final var søkeKriterierStatsborgerskap = personsokKriterier.getStatsborgerskapISO2();
        return person.getStatsborgerskapLandkodeISO2().stream().anyMatch(søkeKriterierStatsborgerskap::contains);
    }

    static boolean harStatsborgerskap(PersonModell personModell, String statsborgerskap) {
        return personModell.getStatsborgerskapLandkodeISO2().stream().anyMatch(statsborgerskap::equals);
    }

    static boolean harStatsborgerskapIListe(PersonModell personModell, Collection<String> statsborgerskapsListe) {
        return personModell.getStatsborgerskapLandkodeISO2().stream().anyMatch(statsborgerskapsListe::contains);
    }

    static boolean harSammeFoedselsdato(PersonModell personModell, PersonsokKriterier personsokKriterier) {
        return harSammeFoedselsdato(personModell, personsokKriterier.getFoedselsdato());
    }

    static boolean harSammeFoedselsdato(PersonModell personModell, LocalDate fødselsdato) {
        return personModell.getFødselsdato().equals(fødselsdato);
    }

    static boolean harUtenlandskID(PersonModell personModell, UtenlandskId utenlandskId) {
        return personModell.getUtenlandskId().stream().anyMatch(utenlandskId::equals);
    }

    public static boolean harUkjentEllerSammeKjønn(PersonModell identifisertPerson, Person sedPerson) {
        return sedPerson.getKjoenn() == Kjønn.U || identifisertPerson.getKjønn() == sedPerson.getKjoenn().tilDomene();
    }

    @java.lang.SuppressWarnings("all")
    private PersonKontroller() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
