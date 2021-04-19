package no.nav.melosys.eessi.repository;

import java.util.Optional;

import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucIdentifiseringOppgRepository extends JpaRepository<BucIdentifiseringOppg, Long> {

    Optional<BucIdentifiseringOppg> findByRinaSaksnummer(String rinaSaksnummer);
    Optional<BucIdentifiseringOppg> findByOppgaveId(String oppgaveID);
}
