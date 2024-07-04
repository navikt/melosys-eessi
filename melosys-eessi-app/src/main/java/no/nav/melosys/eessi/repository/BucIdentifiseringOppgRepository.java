package no.nav.melosys.eessi.repository;

import java.util.Collection;
import java.util.Optional;

import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BucIdentifiseringOppgRepository extends JpaRepository<BucIdentifiseringOppg, Long> {

    Collection<BucIdentifiseringOppg> findByRinaSaksnummer(String rinaSaksnummer);

    Optional<BucIdentifiseringOppg> findByOppgaveId(String oppgaveID);

    @Transactional
    @Modifying
    @Query("update buc_identifisering_oppg b set b.versjon = b.versjon+1 where b.oppgaveId = ?1 and b.rinaSaksnummer = ?2")
    int updateVersjonNumberBy1(String oppgaveId, String rinasaksnummer);
}
