package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.BucIdentifisert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface BucIdentifisertRepository : JpaRepository<BucIdentifisert, Long> {
    fun findByRinaSaksnummer(rinaSaksnummer: String): Optional<BucIdentifisert>

    /**
     * Lagrer identifisert person atomisk. Om rina_saksnummer allerede finnes gjøres ingenting.
     * Dette forhindrer race condition (duplikat unik nøkkel) når flere pods/tråder identifiserer
     * samme rina-sak samtidig. Returnerer antall rader satt inn (1 = ny, 0 = fantes allerede).
     */
    @Modifying
    @Query(
        value = "INSERT INTO buc_identifisert (rina_saksnummer, folkeregisterident) " +
            "VALUES (:rinaSaksnummer, :folkeregisterident) ON CONFLICT (rina_saksnummer) DO NOTHING",
        nativeQuery = true
    )
    fun lagreHvisIkkeEksisterer(
        @Param("rinaSaksnummer") rinaSaksnummer: String,
        @Param("folkeregisterident") folkeregisterident: String
    ): Int
}
