package no.nav.melosys.eessi.controller

import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.SedMottattLager
import no.nav.melosys.eessi.repository.SedMottattLagerRepository
import no.nav.security.token.support.core.api.Protected
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime

@Protected
@RestController
@RequestMapping("/admin/sed-mottatt-lager")
class SedMottattLagerController(
    private val sedMottattLagerRepository: SedMottattLagerRepository
) {

    @GetMapping
    fun getAllSeds(pageable: Pageable): Page<SedMottattLager> {
        return sedMottattLagerRepository.findAll(pageable)
    }

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
    ): List<SedMottattLager> {
        val since = ZonedDateTime.now().minusHours(hours)
        return sedMottattLagerRepository.findByCreatedAtAfter(since)
    }

    @GetMapping("/count")
    fun getCount(): Map<String, Long> {
        return mapOf("count" to sedMottattLagerRepository.count())
    }
}
