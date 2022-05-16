package no.nav.melosys.eessi.service.sts;

import no.nav.melosys.eessi.integration.RestConsumer;

public interface RestSts extends RestConsumer {

    String bearerToken();

    String collectToken();
}
