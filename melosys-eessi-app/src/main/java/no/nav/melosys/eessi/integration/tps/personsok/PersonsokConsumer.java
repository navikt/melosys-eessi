package no.nav.melosys.eessi.integration.tps.personsok;

import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;

public class PersonsokConsumer {

    private PersonsokPortType port;

    public PersonsokConsumer(PersonsokPortType port) {
        this.port = port;
    }

    public FinnPersonResponse finnPerson(FinnPersonRequest request)
            throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput {
        return port.finnPerson(request);
    }
}
