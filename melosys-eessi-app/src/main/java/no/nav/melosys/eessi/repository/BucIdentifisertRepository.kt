package no.nav.melosys.eessi.repository;

import java.util.Optional;

import no.nav.melosys.eessi.models.BucIdentifisert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucIdentifisertRepository extends JpaRepository<BucIdentifisert, Long> {
    Optional<BucIdentifisert> findByRinaSaksnummer(String rinaSaksnummer);
}
