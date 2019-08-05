package no.nav.melosys.eessi.integration;

import java.util.UUID;

public interface UUIDGenerator {

    default String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
