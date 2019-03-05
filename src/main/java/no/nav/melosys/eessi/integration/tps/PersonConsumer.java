package no.nav.melosys.eessi.integration.tps;

import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonConsumer {

    private PersonV3 port;

    public PersonConsumer(PersonV3 port) {
        this.port = port;
    }

    public HentPersonResponse hentPerson(HentPersonRequest request) throws HentPersonSikkerhetsbegrensning, HentPersonPersonIkkeFunnet {
        return port.hentPerson(request);
    }
}
