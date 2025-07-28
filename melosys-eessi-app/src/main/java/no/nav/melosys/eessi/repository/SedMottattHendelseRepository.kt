package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.SedMottattHendelse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface SedMottattHendelseRepository : JpaRepository<SedMottattHendelse, Long> {

    fun findAllByJournalpostIdIsNullOrderByMottattDato(): List<SedMottattHendelse>

    @Query(
        value = "select * from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1 order by mottatt_dato desc",
        nativeQuery = true
    )
    fun findAllByRinaSaksnummerSortedByMottattDatoDesc(rinaSaksnummer: String): List<SedMottattHendelse>

    @Query(
        value = "select * from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1 and publisert_kafka = ?2 order by mottatt_dato",
        nativeQuery = true
    )
    fun findAllByRinaSaksnummerAndPublisertKafkaSortedByMottattDato(rinaSaksnummer: String, publisertKafka: Boolean): List<SedMottattHendelse>

    @Query(
        value = "select * from sed_mottatt_hendelse where mottatt_dato between ?1 and ?2",
        nativeQuery = true
    )
    fun findAllByMottattDatoBetween(startTidspunkt: LocalDateTime, sluttTidspunkt: LocalDateTime): List<SedMottattHendelse>

    @Query(
        value = "select count(*) from sed_mottatt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1",
        nativeQuery = true
    )
    fun countAllByRinaSaksnummer(rinaSaksnummer: String): Int

    @Query(
        value = "select * from sed_mottatt_hendelse where sed_hendelse ->> 'sedId' = ?1",
        nativeQuery = true
    )
    fun findBySedID(sedID: String): Optional<SedMottattHendelse>
}
