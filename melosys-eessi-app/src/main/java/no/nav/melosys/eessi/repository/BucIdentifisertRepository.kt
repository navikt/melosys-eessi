package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.BucIdentifisert
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BucIdentifisertRepository : JpaRepository<BucIdentifisert, Long> {
    fun findByRinaSaksnummer(rinaSaksnummer: String): Optional<BucIdentifisert>
}
