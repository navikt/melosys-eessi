package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.JournalpostSedKobling
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JournalpostSedKoblingRepository : JpaRepository<JournalpostSedKobling, String> {
    fun findByJournalpostID(journalpostID: String): Optional<JournalpostSedKobling>

    fun findByRinaSaksnummer(rinaSaksnummer: String): MutableList<JournalpostSedKobling>
}
