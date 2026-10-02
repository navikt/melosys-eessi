package no.nav.melosys.eessi.controller.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

private val log = KotlinLogging.logger { }

@Component
class AdminTilgangInterceptor(
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    @Value("\${melosys.admin.driftsgruppe}") private val driftsgruppeId: String,
    @Value("\${melosys.admin.console-klient-id}") private val consoleKlientId: String,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // @Protected godtar også STS-token, så adminrutene må kreve Azure-token selv
        val claims = claimsFraGyldigAzureToken()
        if (claims == null) {
            log.warn { "Admin-kall avvist: mangler Azure-token (${request.method})" }
            return avvis(response, 401, MANGLER_AZURE_TOKEN)
        }

        val azp = claims.getStringClaim("azp")
        if (azp != consoleKlientId) {
            log.warn { "Admin-kall avvist: ukjent klient (azp=$azp, ${request.method})" }
            return avvis(response, 403, UKJENT_KLIENT)
        }

        if (erMaskinkall(claims)) return true
        if (erMedlemAvDriftsgruppe(claims)) return true

        log.warn { "Admin-kall avvist: personkall uten driftsgruppe (${request.method})" }
        return avvis(response, 403, MANGLER_DRIFTSGRUPPE)
    }

    // Skrives direkte: RestExceptionHandler gjør ellers alle unntak om til 500
    private fun avvis(response: HttpServletResponse, status: Int, melding: String): Boolean {
        response.status = status
        response.contentType = MediaType.TEXT_PLAIN_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(melding)
        return false
    }

    private fun claimsFraGyldigAzureToken(): JwtTokenClaims? =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(ISSUER)?.jwtTokenClaims

    // Entra setter idtyp = app bare i maskintoken. Mangler den, regnes kallet som personkall.
    private fun erMaskinkall(claims: JwtTokenClaims) = claims.getStringClaim("idtyp") == IDTYP_MASKIN

    // getAsList gir null når groups mangler
    private fun erMedlemAvDriftsgruppe(claims: JwtTokenClaims) = driftsgruppeId in claims.getAsList("groups").orEmpty()

    companion object {
        const val MANGLER_AZURE_TOKEN = "Mangler gyldig token"
        const val UKJENT_KLIENT = "Kallet kommer ikke fra en godkjent klient"
        const val MANGLER_DRIFTSGRUPPE = "Mangler tilgang til admin-endepunkter"
        private const val ISSUER = "aad"
        private const val IDTYP_MASKIN = "app"
    }
}
