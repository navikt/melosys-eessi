package no.nav.melosys.eessi.repository;

import java.util.Collection;
import no.nav.melosys.eessi.models.SedMottatt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedMottattRepository extends JpaRepository<SedMottatt, Long> {
    Collection<SedMottatt> findAllByFerdigFalseAndFeiletFalse();
}
