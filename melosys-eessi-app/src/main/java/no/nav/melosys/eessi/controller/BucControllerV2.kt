package no.nav.melosys.eessi.controller

import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import no.nav.melosys.eessi.controller.dto.BucOgSedOpprettetDto
import no.nav.melosys.eessi.controller.dto.OpprettBucOgSedDtoV2
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.service.sed.SedService
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Protected
@RestController
@RequestMapping("/v2/buc")
class BucControllerV2(
    private val sedService: SedService
) {

    @ApiResponse(
        description = "V2: Oppretter første SED for den spesifikke buc-typen, og sender denne hvis sendAutomatisk=true. " +
                "Sender på eksisterende BUC hvis BUCen meddeler et lovvalg med utenlandsk myndighet, og BUCen er åpen. " +
                "All konfigurasjon sendes i request body."
    )
    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun opprettBucOgSed(
        @Valid @RequestBody request: OpprettBucOgSedDtoV2
    ): BucOgSedOpprettetDto {
        if (request.bucType.hentFørsteLovligeSed().kreverAdresse() && request.sedDataDto.manglerAdresser()) {
            throw ValidationException("Personen mangler adresse")
        }

        return sedService.opprettBucOgSed(request)
    }
}
