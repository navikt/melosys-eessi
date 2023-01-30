package no.nav.melosys.eessi.repository;

import no.nav.melosys.eessi.models.JournalpostSedKobling;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yaml",
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:11:///postgres"
    })
class JournalpostSedKoblingRepositoryTest {

    @Autowired
    private JournalpostSedKoblingRepository journalpostSedKoblingRepository;

    @Test
    void findByRinaSaksnummer_toInnslagIDB_ok() {
        JournalpostSedKobling a009Kobling = lagJournalpostSedKobling("1", "A009");
        JournalpostSedKobling a010Kobling = lagJournalpostSedKobling("2", "A010");
        journalpostSedKoblingRepository.save(a009Kobling);
        journalpostSedKoblingRepository.save(a010Kobling);


        List<JournalpostSedKobling> byRinaSaksnummer = journalpostSedKoblingRepository.findByRinaSaksnummer("123456");


        assertThat(byRinaSaksnummer).hasSize(2);
    }

    @NotNull
    private static JournalpostSedKobling lagJournalpostSedKobling(String journalpostID, String sedType) {
        JournalpostSedKobling kobling = new JournalpostSedKobling();
        kobling.setJournalpostID(journalpostID);
        kobling.setSedType(sedType);
        kobling.setRinaSaksnummer("123456");
        kobling.setSedId("22");
        kobling.setBucType("LA_BUC_02");
        kobling.setSedVersjon("1");

        return kobling;
    }
}
