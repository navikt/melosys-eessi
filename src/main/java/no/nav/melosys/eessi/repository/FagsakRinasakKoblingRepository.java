package no.nav.melosys.eessi.repository;

import java.util.List;
import java.util.Optional;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FagsakRinasakKoblingRepository extends JpaRepository<FagsakRinasakKobling, Long> {

    List<FagsakRinasakKobling> findAllByGsakSaksnummer(Long gsakSaksnummer);

    Optional<FagsakRinasakKobling> findByRinaSaksnummer(String rinaSaksnummer);

    void deleteByRinaSaksnummer(String rinaSaksnummer);
}
