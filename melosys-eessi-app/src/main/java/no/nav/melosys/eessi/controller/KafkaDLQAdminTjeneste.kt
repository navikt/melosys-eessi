package no.nav.melosys.eessi.controller

import mu.KotlinLogging
import no.nav.melosys.eessi.controller.dto.KafkaDLQDto
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.RinaSakOversiktV3
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.SedAnalyseResult
import no.nav.melosys.eessi.models.kafkadlq.KafkaDLQ
import no.nav.melosys.eessi.models.kafkadlq.SedMottattHendelseKafkaDLQ
import no.nav.melosys.eessi.service.buc.BucAdminService
import no.nav.melosys.eessi.service.kafkadlq.KafkaDLQService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

private val log = KotlinLogging.logger { }

@Unprotected
@RestController
@RequestMapping("/admin/kafka/dlq")
class KafkaDLQAdminTjeneste(
    private val kafkaDLQService: KafkaDLQService,
    private val bucAdminService: BucAdminService
) {

    @Value("\${melosys.admin.api-key}")
    private lateinit var apiKey: String

    @GetMapping
    fun hentFeiledeMeldinger(@RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<List<KafkaDLQDto>> {
        validerApikey(apiKey)
        return ResponseEntity.ok(kafkaDLQService.hentFeiledeKafkaMeldinger().map(::mapEntitetTilDto))
    }

    @PostMapping("/{uuid}/restart")
    fun rekjørKafkaMelding(@PathVariable uuid: String, @RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<Void> {
        validerApikey(apiKey)
        kafkaDLQService.rekjørKafkaMelding(UUID.fromString(uuid))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/restart/alle")
    fun rekjørAlleKafkaMeldinger(@RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<Map<String, Any>> {
        validerApikey(apiKey)

        val vellykket = mutableListOf<UUID>()
        val feilet = mutableListOf<String>()

        kafkaDLQService.hentFeiledeKafkaMeldinger().forEach { kafkaDLQ ->
            kafkaDLQ.id?.let { id ->
                try {
                    kafkaDLQService.rekjørKafkaMelding(id)
                    vellykket.add(id)
                } catch (e: Exception) {
                    feilet.add("$id: ${e.message}")
                    log.error("Feil ved rekjøring av melding med ID {}: {}", id, e.message, e)
                }
            } ?: run {
                feilet.add("Melding uten ID")
                log.error("Fant melding uten ID")
            }
        }

        val resultat = mapOf(
            "antallVellykket" to vellykket.size,
            "antallFeilet" to feilet.size,
            "vellykkedeMeldinger" to vellykket,
            "feiledeMeldinger" to feilet
        )

        return ResponseEntity.ok(resultat)
    }

    @GetMapping("/buc/analyse/{rinaSaksnummer}")
    fun analyserSeder(@PathVariable rinaSaksnummer: String, @RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<SedAnalyseResult> {
        validerApikey(apiKey)
        log.info { "Analyserer SEDer for sak $rinaSaksnummer" }
        return ResponseEntity.ok(bucAdminService.analyserManglendeSeder(rinaSaksnummer))
    }

    @GetMapping("/buc/oversikt/{rinaSaksnummer}")
    fun hentRinaOversikt(@PathVariable rinaSaksnummer: String, @RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<RinaSakOversiktV3> {
        validerApikey(apiKey)
        log.info { "Henter RINA oversikt for sak $rinaSaksnummer" }
        return ResponseEntity.ok(bucAdminService.hentRinaOversikt(rinaSaksnummer))
    }

    @PostMapping("/buc/resend/{rinaSaksnummer}/{dokumentId}")
    fun resendSed(@PathVariable rinaSaksnummer: String, @PathVariable dokumentId: String, @RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<Void> {
        validerApikey(apiKey)
        log.info { "Sender SED $dokumentId på nytt for sak $rinaSaksnummer" }
        bucAdminService.resendSed(rinaSaksnummer, dokumentId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/buc/analyse/alle")
    fun analyserAlleFeiledeSaker(@RequestHeader(API_KEY_HEADER) apiKey: String): ResponseEntity<List<SedAnalyseResult>> {
        validerApikey(apiKey)
        log.info { "Starter analyse av alle saker med feilede meldinger i DLQ" }

        val rinaSaksnumre = mutableSetOf<String>()
        val feiledeMeldinger: List<KafkaDLQ> = kafkaDLQService.hentFeiledeKafkaMeldinger()

        for (melding: KafkaDLQ in feiledeMeldinger) {
            if (melding is SedMottattHendelseKafkaDLQ) {
                val sedHendelse: no.nav.melosys.eessi.kafka.consumers.SedHendelse? = melding.sedMottattHendelse
                sedHendelse?.rinaSakId?.let { rinaSaksnumre.add(it) }
            }
        }

        log.info { "Fant ${rinaSaksnumre.size} unike rina-saksnumre å analysere" }

        val analyseResultater = mutableListOf<SedAnalyseResult>()
        for (rinaSaksnummer in rinaSaksnumre) {
            try {
                val analyse: SedAnalyseResult = bucAdminService.analyserManglendeSeder(rinaSaksnummer)
                analyseResultater.add(analyse)
            } catch (e: Exception) {
                log.error(e) { "Feil ved analyse av rina-sak $rinaSaksnummer" }
            }
        }

        return ResponseEntity.ok(analyseResultater)
    }

    private fun mapEntitetTilDto(entitet: KafkaDLQ): KafkaDLQDto =
        KafkaDLQDto.builder()
            .id(entitet.id.toString())
            .queueType(entitet.queueType!!)
            .sisteFeilmelding(entitet.sisteFeilmelding)
            .tidRegistrert(entitet.tidRegistrert)
            .tidSistRekjort(entitet.tidSistRekjort)
            .antallRekjoringer(entitet.antallRekjoringer)
            .melding(entitet.hentMeldingSomStreng())
            .skip(entitet.skip)
            .build()

    private fun validerApikey(value: String) {
        if (apiKey != value) {
            throw SecurityException("Ugyldig API-nøkkel")
        }
    }

    companion object {
        private const val API_KEY_HEADER = "X-MELOSYS-ADMIN-APIKEY"
    }
}
