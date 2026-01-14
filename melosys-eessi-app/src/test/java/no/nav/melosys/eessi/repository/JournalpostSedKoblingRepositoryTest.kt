package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.JournalpostSedKobling
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(
    locations = ["classpath:application-test.yaml"],
    properties = ["spring.datasource.url=jdbc:tc:postgresql:11:///postgres"]
)
class JournalpostSedKoblingRepositoryTest(
    @Autowired private val journalpostSedKoblingRepository: JournalpostSedKoblingRepository
) {


    @Test
    fun findByRinaSaksnummer_toInnslagIDB_ok() {
        val a009Kobling = lagJournalpostSedKobling("1", "A009")
        val a010Kobling = lagJournalpostSedKobling("2", "A010")
        journalpostSedKoblingRepository.save(a009Kobling)
        journalpostSedKoblingRepository.save(a010Kobling)

        val byRinaSaksnummer = journalpostSedKoblingRepository.findByRinaSaksnummer("123456")

        Assertions.assertThat(byRinaSaksnummer).hasSize(2)
    }

    private fun lagJournalpostSedKobling(journalpostID: String, sedType: String) =
        JournalpostSedKobling(
            journalpostID = journalpostID,
            sedType = sedType,
            rinaSaksnummer = "123456",
            sedId = "22",
            bucType = "LA_BUC_02",
            sedVersjon = "1"
        )
}
