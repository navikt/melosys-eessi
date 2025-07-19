package no.nav.melosys.eessi.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.repository.SedMottattLager
import no.nav.melosys.eessi.repository.SedMottattLagerRepository
import no.nav.security.token.support.core.api.Protected
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrNull

@Protected
@RestController
@RequestMapping("/admin/sed-mottatt-lager")
class SedMottattLagerController(
    private val sedMottattLagerRepository: SedMottattLagerRepository,
    private val sedMottattHendelseRepository: SedMottattHendelseRepository
) {

    @GetMapping
    @Parameter(
        name = "sort",
        description = "Sort criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.",
        example = "createdAt,desc",
        `in` = ParameterIn.QUERY,
        array = ArraySchema(
            schema = Schema(
                type = "string",
                allowableValues = [
                    "id", "id,asc", "id,desc",
                    "sedId", "sedId,asc", "sedId,desc",
                    "storageReason", "storageReason,asc", "storageReason,desc",
                    "createdAt", "createdAt,asc", "createdAt,desc"
                ]
            )
        )
    )
    fun getAllSeds(pageable: Pageable): Page<SedMottattLager> = sedMottattLagerRepository.findAll(pageable)

    @GetMapping("/{id}")
    fun getSedById(@PathVariable id: Long): ResponseEntity<SedMottattLager> =
        sedMottattLagerRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @GetMapping("/{id}/sed")
    fun getSedContentById(@PathVariable id: Long): ResponseEntity<SED> {
        return sedMottattLagerRepository.findById(id)
            .map { ResponseEntity.ok(it.sed) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/sed-id/{sedId}")
    fun getSedsBySedId(@PathVariable sedId: String): List<SedMottattLager> {
        return sedMottattLagerRepository.findBySedId(sedId)
    }

    @GetMapping("/recent")
    fun getRecentSeds(
        @RequestParam(defaultValue = "24") hours: Long
    ): List<Map<String, Any?>> {
        val since = ZonedDateTime.now().minusHours(hours)
        val recentSeds = sedMottattLagerRepository.findByCreatedAtAfter(since)

        return recentSeds.map { sedLager ->
            val hendelse = sedMottattHendelseRepository.findBySedID(sedLager.sedId)
            mapOf(
                "sedMottattLager" to sedLager,
                "rinaSakId" to hendelse.getOrNull()?.sedHendelse?.rinaSakId
            )
        }
    }

    @GetMapping("/count")
    fun getCount(): Map<String, Long> {
        return mapOf("count" to sedMottattLagerRepository.count())
    }
}
