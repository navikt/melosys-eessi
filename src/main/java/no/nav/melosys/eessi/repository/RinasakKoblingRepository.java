package no.nav.melosys.eessi.repository;

import java.util.Optional;
import no.nav.melosys.eessi.models.RinasakKobling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RinasakKoblingRepository extends JpaRepository<RinasakKobling, Long> {

    Optional<RinasakKobling> findByRinaId(String rinaId);

    void deleteByRinaId(String rinaId);
}
