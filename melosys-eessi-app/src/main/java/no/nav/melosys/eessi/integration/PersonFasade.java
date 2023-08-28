package no.nav.melosys.eessi.integration;

import java.util.List;

import no.nav.melosys.eessi.integration.pdl.dto.sed.DnummerRekvisjonTilMellomlagring;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.personsok.PersonsokKriterier;

public interface PersonFasade {

    String hentPreutfylltLenkeForRekvirering(DnummerRekvisjonTilMellomlagring dnummerRekvisjonTilMellomlagring);
    PersonModell hentPerson(String ident);
    String hentAktoerId(String ident);
    String hentNorskIdent(String aktoerID);
    List<PersonSokResponse> soekEtterPerson(PersonsokKriterier personsokKriterier);
}
