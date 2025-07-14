package no.nav.melosys.eessi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface SedMottattStorageRepository : JpaRepository<SedMottattStorage, Long> {

    fun findBySedId(sedId: String): List<SedMottattStorage>

    fun findByRinaSaksnummer(rinaSaksnummer: String): List<SedMottattStorage>

    fun findByStorageReason(storageReason: String): List<SedMottattStorage>

    fun findByCreatedAtBetween(start: ZonedDateTime, end: ZonedDateTime): List<SedMottattStorage>
}
