package no.nav.melosys.eessi.repository;

import java.util.List;

import no.nav.melosys.eessi.models.SedMottattHendelse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SedMottattHendelseRepository extends JpaRepository<SedMottattHendelse, Long> {


    List<SedMottattHendelse> findAllByJournalpostIdIsNullOrderByMottattDato();

    @Query(
            value = "select * from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1 and publisert_kafka = ?2 and sed_hendelse ->> 'sedType' <> ?3 order by mottatt_dato",
            nativeQuery = true)
    List<SedMottattHendelse> findAllByRinaSaksnummerAndPublisertKafkaAndNotX100SortedByMottattDato(String rinaSaksnummer, boolean publisertKafka, String sedType);

    @Query(
            value = "select count(*) from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1",
            nativeQuery = true)
    int countAllByRinaSaksnummer(String rinaSaksnummer);

}
