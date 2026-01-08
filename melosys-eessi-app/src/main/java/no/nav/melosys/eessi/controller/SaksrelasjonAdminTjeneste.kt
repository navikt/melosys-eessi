package no.nav.melosys.eessi.controller

import io.swagger.v3.oas.annotations.responses.ApiResponse
import no.nav.melosys.eessi.controller.dto.OppdaterGsakSaksnummerDto
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonAdminService
import no.nav.security.token.support.core.api.Protected
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Protected
@RestController
@RequestMapping("/admin/saksrelasjon")
class SaksrelasjonAdminTjeneste(
    @Value("\${melosys.admin.api-key}") private val apiKey: String,
    private val saksrelasjonAdminService: SaksrelasjonAdminService
) {

    @PutMapping("/gsaknummer")
    @ApiResponse(description = "Oppdaterer gsakSaksnummer for en gitt rinaSaksnummer")
    fun oppdaterGsakSaksnummer(
        @RequestHeader(API_KEY_HEADER) apiKey: String,
        @RequestBody request: OppdaterGsakSaksnummerDto
    ): ResponseEntity<String> {
        
        validerApikey(apiKey)
        
        saksrelasjonAdminService.oppdaterGsakSaksnummer(request.rinaSaksnummer, request.nyGsakSaksnummer)
        
        return ResponseEntity.ok("Gsaknummer oppdatert for rinaSaksnummer: ${request.rinaSaksnummer}")
    }

    private fun validerApikey(value: String) {
        if (apiKey != value) {
            throw SecurityException("Ugyldig API-nøkkel")
        }
    }

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
    }
}