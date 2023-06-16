package no.nav.melosys.eessi.repository;

import no.nav.melosys.eessi.models.SedMottattHendelse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SedSendtHendelseRepository extends JpaRepository<SedMottattHendelse, Long> {


    List<SedMottattHendelse> findAllByJournalpostIdIsNullOrderByMottattDato();

    @Query(
            value = "select * from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1 and publisert_kafka = ?2 order by mottatt_dato",
            nativeQuery = true)
    List<SedMottattHendelse> findAllByRinaSaksnummerAndPublisertKafkaSortedByMottattDato(String rinaSaksnummer, boolean publisertKafka);

    @Query(
            value = "select count(*) from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1",
            nativeQuery = true)
    int countAllByRinaSaksnummer(String rinaSaksnummer);

    @Query(
            value = "select * from sed_mottatt_hendelse where sed_hendelse ->> 'sedId' = ?1",
            nativeQuery = true)
    Optional<SedMottattHendelse> findBySedID(String sedID);

}
