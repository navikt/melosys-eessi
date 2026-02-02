package no.nav.melosys.eessi.service.saksrelasjon

import mu.KotlinLogging
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

private val log = KotlinLogging.logger {}

@Service
class SaksrelasjonAdminService(
    private val fagsakRinasakKoblingRepository: FagsakRinasakKoblingRepository
) {

    @Transactional
    fun oppdaterGsakSaksnummer(rinaSaksnummer: String, nyGsakSaksnummer: Long) {
        validerInput(rinaSaksnummer, nyGsakSaksnummer)

        val eksisterendeKobling = fagsakRinasakKoblingRepository.findByRinaSaksnummer(rinaSaksnummer)
            .orElseThrow { ValidationException("Fant ikke rinaSaksnummer: $rinaSaksnummer") }

        val gammelGsakSaksnummer = eksisterendeKobling.gsakSaksnummer

        log.info { "Oppdaterer gsakSaksnummer for rinaSaksnummer $rinaSaksnummer fra $gammelGsakSaksnummer til $nyGsakSaksnummer" }

        eksisterendeKobling.gsakSaksnummer = nyGsakSaksnummer
        fagsakRinasakKoblingRepository.save(eksisterendeKobling)

        log.info { "Oppdatert gsakSaksnummer for rinaSaksnummer $rinaSaksnummer fra $gammelGsakSaksnummer til $nyGsakSaksnummer" }
    }

    private fun validerInput(rinaSaksnummer: String, nyGsakSaksnummer: Long) {
        if (!StringUtils.hasText(rinaSaksnummer)) {
            throw ValidationException("rinaSaksnummer kan ikke være tom")
        }

        if (nyGsakSaksnummer < 1L) {
            throw ValidationException("nyGsakSaksnummer må være større enn 0")
        }
    }
}
