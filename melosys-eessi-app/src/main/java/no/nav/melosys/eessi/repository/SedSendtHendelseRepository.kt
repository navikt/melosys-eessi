package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.SedSendtHendelse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SedSendtHendelseRepository : JpaRepository<SedSendtHendelse, Long> {
    @Query(value = "select * from sed_sendt_hendelse where sed_hendelse ->> 'rinaSakId' = ?1", nativeQuery = true)
    fun findAllByRinaSaksnummerAndAndJournalpostIdIsNull(rinaSaksnummer: String): MutableList<SedSendtHendelse>
}
