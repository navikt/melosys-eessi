package no.nav.melosys.eessi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface SedMottattLagerRepository : JpaRepository<SedMottattLager, Long> {

    fun findBySedId(sedId: String): List<SedMottattLager>

    fun findByCreatedAtAfter(since: ZonedDateTime): List<SedMottattLager>
}
