package no.nav.melosys.eessi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.melosys.eessi.security.ThreadLocalAccessInfo
import no.nav.melosys.eessi.service.mottak.SedMottakService
import no.nav.security.token.support.core.api.Protected
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@Protected
@RestController
@RequestMapping("/admin/identifiseringsoppgave")
class IdentifiseringsoppgaveAdminTjeneste(
    private val sedMottakService: SedMottakService,
    @Value("\${melosys.admin.api-key}") private val apiKey: String
) {

    @Operation(
        summary = "Opprett oppgave til ID og fordeling for en upublisert A-SED",
        description = """
            Oppretter journalpost (hvis den ikke allerede finnes) og oppgave til ID og fordeling (enhet 4303)
            for den nyeste A-SEDen på angitt rinasak.

            Forutsetninger som valideres:
            - Det finnes en A-SED for rinasaken i sed_mottatt_hendelse
            - A-SEDen er IKKE publisert på Kafka (publisert_kafka = false)
            - Det finnes ingen åpen identifiseringsoppgave for rinasaken fra før

            Brukes når den ordinære mottaksflyten ikke fikk opprettet identifiseringsoppgaven.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Oppgaven ble opprettet"),
            ApiResponse(responseCode = "400", description = "A-SEDen er allerede publisert, eller det finnes en åpen oppgave fra før"),
            ApiResponse(responseCode = "404", description = "Fant ingen A-SED for rinasaken")
        ]
    )
    @PostMapping("/{rinaSaksnummer}")
    fun opprettIdentifiseringsoppgave(
        @RequestHeader(API_KEY_HEADER) apiKeyHeader: String,
        @Parameter(description = "Rinasaksnummeret A-SEDen tilhører") @PathVariable rinaSaksnummer: String
    ): ResponseEntity<SedMottakService.IdentifiseringsoppgaveResultat> {
        validerApikey(apiKeyHeader)
        require(rinaSaksnummer.isNotBlank()) { "rinaSaksnummer kan ikke være tomt" }

        log.info { "Admin: oppretter oppgave til ID og fordeling for rinasak $rinaSaksnummer" }

        return ThreadLocalAccessInfo.utførSomAdminForespørsel {
            ResponseEntity.ok(sedMottakService.opprettIdentifiseringsoppgaveForUpublisertASed(rinaSaksnummer))
        }
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
