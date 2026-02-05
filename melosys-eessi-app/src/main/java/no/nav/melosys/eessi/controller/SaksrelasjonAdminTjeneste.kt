package no.nav.melosys.eessi.controller

import no.nav.melosys.eessi.controller.dto.SaksrelasjonDto
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Protected
@RestController
@RequestMapping("/admin/saksrelasjon")
class SaksrelasjonAdminTjeneste(
    private val saksrelasjonService: SaksrelasjonService,
    @Value("\${melosys.admin.api-key}") private val apiKey: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PutMapping("/kobling/{rinaSaksnummer}")
    fun oppdaterKobling(
        @RequestHeader(API_KEY_HEADER) apiKeyHeader: String,
        @PathVariable rinaSaksnummer: String,
        @RequestBody dto: SaksrelasjonDto
    ): ResponseEntity<Void> {
        validerApikey(apiKeyHeader)
        requireNotNull(dto.gsakSaksnummer) { "gsakSaksnummer kan ikke være null" }
        require(dto.gsakSaksnummer!! > 0) { "gsakSaksnummer må være større enn 0" }

        log.info(
            "Admin: Oppdaterer kobling for rinaSaksnummer {} til gsakSaksnummer {}",
            rinaSaksnummer, dto.gsakSaksnummer
        )
        saksrelasjonService.oppdaterKobling(rinaSaksnummer, dto.gsakSaksnummer!!)
        return ResponseEntity.ok().build()
    }

    private fun validerApikey(value: String) {
        if (apiKey != value) {
            throw SecurityException("Trenger gyldig apikey")
        }
    }

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
    }
}
