package no.nav.melosys.eessi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import mu.KotlinLogging
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.security.ThreadLocalAccessInfo
import no.nav.melosys.eessi.service.mottak.SedMottakAdminService
import no.nav.security.token.support.core.api.Protected
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private val log = KotlinLogging.logger {}

@Protected
@RestController
@RequestMapping("/admin/identifiseringsoppgave")
class IdentifiseringsoppgaveAdminTjeneste(
    private val sedMottakAdminService: SedMottakAdminService,
    @Value("\${melosys.admin.api-key}") private val apiKey: String
) {

    @Operation(
        summary = "Opprett oppgave til ID og fordeling for en upublisert A-SED",
        description = """
            Oppretter journalpost og oppgave til ID og fordeling (enhet 4303) for nyeste A-SED på rinasaken.
            Brukes når den ordinære mottaksflyten ikke fikk opprettet identifiseringsoppgaven.

            Avvises med 400 hvis A-SEDen allerede er publisert på Kafka, eller hvis det finnes en åpen
            identifiseringsoppgave på saken. Ferdigstilte, feilregistrerte og slettede oppgaver blokkerer ikke.
            Svaret inneholder tidligere oppgaver på saken med id og status.
        """
    )
    @PostMapping("/{rinaSaksnummer}")
    fun opprettIdentifiseringsoppgave(
        @RequestHeader(API_KEY_HEADER) apiKeyHeader: String,
        @Parameter(description = "Rinasaksnummeret A-SEDen tilhører") @PathVariable rinaSaksnummer: String
    ): ResponseEntity<SedMottakAdminService.IdentifiseringsoppgaveResultat> {
        validerApikey(apiKeyHeader)
        if (rinaSaksnummer.isBlank()) {
            throw ValidationException("rinaSaksnummer kan ikke være tomt")
        }
        log.info { "Admin: oppretter oppgave til ID og fordeling for rinasak $rinaSaksnummer" }

        return ThreadLocalAccessInfo.utførSomAdminForespørsel {
            ResponseEntity.ok(sedMottakAdminService.opprettIdentifiseringsoppgaveForUpublisertASed(rinaSaksnummer))
        }
    }

    private fun validerApikey(value: String) {
        if (!MessageDigest.isEqual(apiKey.toByteArray(StandardCharsets.UTF_8), value.toByteArray(StandardCharsets.UTF_8))) {
            throw SecurityException("Trenger gyldig apikey")
        }
    }

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
    }
}
