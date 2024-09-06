package no.nav.melosys.eessi.repository

import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FagsakRinasakKoblingRepository : JpaRepository<FagsakRinasakKobling, Long> {
    fun findAllByGsakSaksnummer(gsakSaksnummer: Long): MutableList<FagsakRinasakKobling>

    fun findByRinaSaksnummer(rinaSaksnummer: String): Optional<FagsakRinasakKobling>

    fun deleteByRinaSaksnummer(rinaSaksnummer: String)

    fun findAllByGsakSaksnummerAndBucType(gsakSaksnummer: Long, bucType: BucType): MutableList<FagsakRinasakKobling>
}
