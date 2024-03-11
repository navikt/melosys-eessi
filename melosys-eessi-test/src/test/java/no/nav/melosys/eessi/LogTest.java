package no.nav.melosys.eessi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Slf4j
class LogTest {

    @Test
    @Disabled("for å kjøre lokalt å teste maskering av personnummer i logg")
    void test() {
        // bytt til stdout_json for å teste at personnummer blir maskert
        log.info("test: 12345678901");
    }
}
