package no.nav.melosys.eessi.integration.pdl;

import java.util.List;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
public class PDLService implements PersonFasade {

    private static final String IKKE_IMPLEMENTERT = "Ikke implementert";

    @Override
    public PersonModell hentPerson(String ident) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }

    @Override
    public String hentAktoerId(String ident) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }

    @Override
    public String hentNorskIdent(String aktoerID) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }

    @Override
    public List<PersonSoekResponse> soekEtterPerson(PersonsoekKriterier personsoekKriterier) {
        throw new NotImplementedException(IKKE_IMPLEMENTERT);
    }
}
