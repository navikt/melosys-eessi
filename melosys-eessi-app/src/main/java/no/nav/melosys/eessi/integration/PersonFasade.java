package no.nav.melosys.eessi.integration;

import java.util.List;

import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;

public interface PersonFasade {
    PersonModell hentPerson(String ident);
    String hentAktoerId(String ident);
    String hentNorskIdent(String aktoerID);
    List<PersonSoekResponse> soekEtterPerson(PersonsoekKriterier personsoekKriterier);
}
