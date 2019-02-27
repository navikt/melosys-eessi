package no.nav.melosys.eessi.integration;

import java.util.UUID;

public interface RestConsumer {

    default String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
