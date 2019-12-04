package no.nav.melosys.eessi.integration.tps.personsok;

import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault1;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;

public class PersonsokConsumer {

    private PersonsokPortType port;

    public PersonsokConsumer(PersonsokPortType port) {
        this.port = port;
    }

    public FinnPersonResponse finnPerson(FinnPersonRequest request) throws FinnPersonFault1, FinnPersonFault {
        return port.finnPerson(request);
    }
}
