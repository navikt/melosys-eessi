package no.nav.melosys.eessi.repository;

import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.models.JournalpostSedKobling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalpostSedKoblingRepository extends JpaRepository<JournalpostSedKobling, String> {

    Optional<JournalpostSedKobling> findByJournalpostID(String journalpostID);

    List<JournalpostSedKobling> findByRinaSaksnummer(String rinaSaksnummer);
}
