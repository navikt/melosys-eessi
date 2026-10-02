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
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val claims = claimsFraGyldigAzureToken() ?: return true

        // Uten Azure-token: @Protected og nøkkelsjekken avgjør, som før
        if (erMaskinkall(claims)) return true
        if (erMedlemAvDriftsgruppe(claims)) return true

        log.warn { "Admin-kall avvist: personkall uten driftsgruppe (${request.method})" }
        // Skrives direkte: RestExceptionHandler gjør ellers alle unntak om til 500
        response.status = 403
        response.contentType = MediaType.TEXT_PLAIN_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(MANGLER_DRIFTSGRUPPE)
        return false
    }

    private fun claimsFraGyldigAzureToken(): JwtTokenClaims? =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(ISSUER)?.jwtTokenClaims

    // Entra setter idtyp = app bare i maskintoken. Mangler den, regnes kallet som personkall.
    private fun erMaskinkall(claims: JwtTokenClaims) = claims.getStringClaim("idtyp") == IDTYP_MASKIN

    // getAsList gir null når groups mangler
    private fun erMedlemAvDriftsgruppe(claims: JwtTokenClaims) = driftsgruppeId in claims.getAsList("groups").orEmpty()

    companion object {
        const val MANGLER_DRIFTSGRUPPE = "Mangler tilgang til admin-endepunkter"
        private const val ISSUER = "aad"
        private const val IDTYP_MASKIN = "app"
    }
}
