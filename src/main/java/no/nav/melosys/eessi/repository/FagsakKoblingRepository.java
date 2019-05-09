package no.nav.melosys.eessi.repository;

import java.util.Optional;
import no.nav.melosys.eessi.models.FagsakKobling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FagsakKoblingRepository extends JpaRepository<FagsakKobling, Long> {

    Optional<FagsakKobling> findByGsakSaksnummer(Long gsakSaksnummer);
}
