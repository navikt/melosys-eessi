package no.nav.melosys.eessi.service.journalpostkobling;

import java.util.Optional;
import no.nav.melosys.eessi.models.JournalpostSedKobling;
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository;
import org.springframework.stereotype.Service;

@Service
public class JournalpostSedKoblingService {

    private final JournalpostSedKoblingRepository journalpostSedKoblingRepository;

    public JournalpostSedKoblingService(
            JournalpostSedKoblingRepository journalpostSedKoblingRepository) {
        this.journalpostSedKoblingRepository = journalpostSedKoblingRepository;
    }

    public Optional<JournalpostSedKobling> finnVedJournalpostID(String journalpostID) {
        return journalpostSedKoblingRepository.findByJournalpostID(journalpostID);
    }

    public JournalpostSedKobling lagre(String journalpostID, String rinaSaksnummer, String sedID, String sedVersjon) {
        return journalpostSedKoblingRepository.save(
          new JournalpostSedKobling(journalpostID,rinaSaksnummer, sedID, sedVersjon)
        );
    }

}
