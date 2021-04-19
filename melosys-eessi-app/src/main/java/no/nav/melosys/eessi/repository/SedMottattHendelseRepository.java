package no.nav.melosys.eessi.repository;

import java.util.List;

import no.nav.melosys.eessi.models.SedMottattHendelse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedMottattHendelseRepository extends JpaRepository<SedMottattHendelse, Long> {

    List<SedMottattHendelse> findAllByPublisertKafka(boolean publisertKafka);

}
