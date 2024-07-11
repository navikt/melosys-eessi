package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.BucIdentifiseringOppg
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface BucIdentifiseringOppgRepository : JpaRepository<BucIdentifiseringOppg, Long> {
    fun findByRinaSaksnummer(rinaSaksnummer: String): MutableCollection<BucIdentifiseringOppg>

    fun findByOppgaveId(oppgaveID: String): Optional<BucIdentifiseringOppg>

    @Transactional
    @Modifying
    @Query("update buc_identifisering_oppg b set b.versjon = b.versjon+1 where b.oppgaveId = ?1 and b.rinaSaksnummer = ?2")
    fun updateVersjonNumberBy1(oppgaveId: String, rinasaksnummer: String): Int
}
