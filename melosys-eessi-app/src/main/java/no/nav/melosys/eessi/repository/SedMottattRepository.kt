package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.SedMottatt
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Erstattet av SedMottattHendelseRepository
 */
@Deprecated("")
interface SedMottattRepository : JpaRepository<SedMottatt, Long> {
    fun findAllByFerdigFalseAndFeiletFalse(): MutableCollection<SedMottatt>

    fun countByFeiletIsTrue(): Double?
}
