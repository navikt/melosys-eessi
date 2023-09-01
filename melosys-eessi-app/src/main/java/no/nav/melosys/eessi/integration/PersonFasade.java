package no.nav.melosys.eessi.integration;

import java.util.List;

import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.IdentRekvisisjonTilMellomlagring;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;

public interface PersonFasade {

    String opprettLenkeForRekvirering(IdentRekvisisjonTilMellomlagring identRekvisisjonTilMellomlagring);
    PersonModell hentPerson(String ident);
    String hentAktoerId(String ident);
    String hentNorskIdent(String aktoerID);
    List<PersonSokResponse> soekEtterPerson(PersonsokKriterier personsokKriterier);
}
