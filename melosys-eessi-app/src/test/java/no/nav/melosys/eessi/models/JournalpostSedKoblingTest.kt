package no.nav.melosys.eessi.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JournalpostSedKoblingTest {


    @Test
    void erASed_ok() {
        JournalpostSedKobling journalpostSedKobling = new JournalpostSedKobling();
        journalpostSedKobling.setSedType("A009");

        assertThat(journalpostSedKobling.erASed()).isTrue();
    }
}
