package no.nav.melosys.eessi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Protected
@RestController
@RequestMapping("/admin/saksrelasjon")
class SaksrelasjonAdminTjeneste(
    private val saksrelasjonService: SaksrelasjonService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "Flytt Rinasak-kobling til annen fagsak",
        description = """
            Flytter en eksisterende kobling mellom en Rinasak og en fagsak til en ny fagsak.
            Brukes når en Rinasak er feilaktig koblet til feil fagsak.

            NB: Den gamle fagsaken mister koblingen til Rinasakien - det finnes kun én kobling per Rinasak.
        """
    )
    @PutMapping("/kobling/{rinaSaksnummer}/{gsakSaksnummer}")
    fun oppdaterKobling(
        @Parameter(description = "Rinasak-nummeret som skal flyttes") @PathVariable rinaSaksnummer: String,
        @Parameter(description = "Ny fagsak (gsakSaksnummer) som Rinasakien skal kobles til") @PathVariable gsakSaksnummer: Long
    ): ResponseEntity<OppdaterKoblingResponse> {
        require(gsakSaksnummer > 0) { "gsakSaksnummer må være større enn 0" }

        log.info(
            "Admin: Oppdaterer kobling for rinaSaksnummer {} til gsakSaksnummer {}",
            rinaSaksnummer, gsakSaksnummer
        )
        val gammelGsakSaksnummer = saksrelasjonService.oppdaterKobling(rinaSaksnummer, gsakSaksnummer)

        return ResponseEntity.ok(
            OppdaterKoblingResponse(
                melding = "Flyttet rinaSaksnummer $rinaSaksnummer fra gsakSaksnummer $gammelGsakSaksnummer til $gsakSaksnummer",
                rinaSaksnummer = rinaSaksnummer,
                gammelGsakSaksnummer = gammelGsakSaksnummer,
                nyGsakSaksnummer = gsakSaksnummer
            )
        )
    }

    data class OppdaterKoblingResponse(
        val melding: String,
        val rinaSaksnummer: String,
        val gammelGsakSaksnummer: Long,
        val nyGsakSaksnummer: Long
    )
}
