package no.nav.melosys.eessi.repository;

import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.SedSendtHendelse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SedSendtHendelseRepository extends JpaRepository<SedSendtHendelse, Long> {

    @Query(
        value = "select * from sed_sendt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1",
        nativeQuery = true)
    List<SedSendtHendelse> findAllByRinaSaksnummerAndAndJournalpostIdIsNull(String rinaSaksnummer);
}
