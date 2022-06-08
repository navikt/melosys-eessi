package no.nav.melosys.eessi.service.sts;

import no.nav.melosys.eessi.integration.RestConsumer;

public interface RestStsClient extends RestConsumer {

    String bearerToken();

    String collectToken();
}
