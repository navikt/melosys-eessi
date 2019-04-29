package no.nav.melosys.eessi.repository;

import java.util.Optional;
import no.nav.melosys.eessi.models.CaseRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRelationRepository extends JpaRepository<CaseRelation, Long> {

    Optional<CaseRelation> findByRinaId(String rinaId);

    Optional<CaseRelation> findByGsakSaksnummer(Long gsakSaksnummer);

    void deleteByRinaId(String rinaId);
}
