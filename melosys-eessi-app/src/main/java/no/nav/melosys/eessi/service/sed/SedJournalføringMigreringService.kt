package no.nav.melosys.eessi.service.sed

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer
import no.nav.melosys.eessi.models.SedMottattHendelse
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import kotlin.concurrent.Volatile

private val log = KotlinLogging.logger {}

@Service
class SedJournalføringMigreringService(
    private val sedMottattHendelseRepository: SedMottattHendelseRepository,
    private val euxConsumer: EuxConsumer,
    private val jsonMapper: JsonMapper,
) {
    private val lock = Any()

    var naisClusterName: String = System.getenv().getOrDefault("NAIS_CLUSTER_NAME", "prod-fss")

    private val startTidspunktProd: LocalDateTime = LocalDateTime.of(2024, 4, 18, 13, 37)
    private val sluttTidspunktProd: LocalDateTime = LocalDateTime.of(2024, 4, 24, 15, 37)
    private val fileNameProd: String = "migrering-sed-sendt-prod.json"

    private val startTidspunktDev: LocalDateTime = LocalDateTime.of(2024, 4, 1, 13, 37)
    private val sluttTidspunktDev: LocalDateTime = LocalDateTime.of(2024, 4, 25, 15, 37)
    private val fileNameDev: String = "migrering-sed-sendt-dev.json"

    private val sedMottattMigreringRapportDtoList: MutableList<SedMottattMigreringRapportDto?> = mutableListOf()

    @Volatile
    var erKartleggingSedMottattPågående: Boolean = false
    private var antallSedMottattHendelser = 0
    private var antallSedMottattSjekket = 0

    private val sedSendtMigreringRapportDtoList: MutableList<SedSendtMigreringRapportDto?> = mutableListOf()

    @Volatile
    var erKartleggingSedSendtPågående: Boolean = false
    private var antallSedSendtHendelser = 0
    private var antallSedSendtSjekket = 0

    @Async
    fun startKartleggingAvSedMottatt() {
        synchronized(lock) {
            val (startTidspunkt, sluttTidspunkt) = if (naisClusterName == "prod-fss") {
                startTidspunktProd to sluttTidspunktProd
            } else {
                startTidspunktDev to sluttTidspunktDev
            }

            val sedMottattHendelseListe = sedMottattHendelseRepository.findAllByMottattDatoBetween(startTidspunkt, sluttTidspunkt)
            erKartleggingSedMottattPågående = true
            antallSedMottattHendelser = sedMottattHendelseListe.size
            antallSedMottattSjekket = 0
            sedMottattMigreringRapportDtoList.clear()

            log.info(
                "Starter rapportering av sed med vedlegg fra {} til {}. Antall SedMottattHendelser {}",
                startTidspunkt,
                sluttTidspunkt,
                antallSedMottattHendelser
            )

            for (sedMottattHendelse in sedMottattHendelseListe) {
                if (!erKartleggingSedMottattPågående) break
                kartleggForSedMottattHendelse(sedMottattHendelse)
            }

            erKartleggingSedMottattPågående = false
        }
    }

    @Async
    fun startKartleggingAvSedSendt() {
        synchronized(lock) {
            erKartleggingSedSendtPågående = true
            antallSedSendtSjekket = 0

            val fileName = if (naisClusterName == "prod-fss") fileNameProd else fileNameDev
            val fileUri = requireNotNull(javaClass.classLoader.getResource(fileName)).toURI()
            val content = Files.readString(Paths.get(fileUri))
            val sedSendtJournalføringListe =
                jsonMapper.readValue(content, object : TypeReference<MutableList<SedSendtJournalføringMigrering?>?>() {
                })

            antallSedSendtHendelser = sedSendtJournalføringListe!!.size
            log.info("Starter rapportering av sed sendt med vedlegg fra $fileName. Antall SedSendtJournalføring $antallSedSendtHendelser")

            for (sedSendtJournalføring in sedSendtJournalføringListe) {
                if (!erKartleggingSedSendtPågående) break

                val rinaSaksnummer = sedSendtJournalføring!!.rinaSakId
                val dokumentId = sedSendtJournalføring.rinaDokumentId
                val sedMedVedlegg = euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId)

                if (sedMedVedlegg.vedlegg!!.isNotEmpty()) {
                    log.info("Fant vedlegg for sed med rinaSaksnummer {}, dokumentId {}", rinaSaksnummer, dokumentId)
                    sedSendtMigreringRapportDtoList.add(SedSendtMigreringRapportDto(rinaSaksnummer, dokumentId))
                }
                antallSedSendtSjekket++
            }
        }
    }

    fun stoppSedMottattKartlegging() {
        log.info(
            "Stopp rapportering av sed mottatt med vedlegg. Sjekket {} SED. Funnet {} av {} sed med vedlegg.",
            antallSedMottattSjekket,
            sedMottattMigreringRapportDtoList.size,
            antallSedMottattHendelser
        )
        erKartleggingSedMottattPågående = false
    }

    fun stoppSedMendtKartlegging() {
        log.info(
            "Stopp rapportering av sed sendt med vedlegg. Sjekket {} SED. Funnet {} av {} sed med vedlegg.",
            antallSedSendtSjekket,
            sedSendtMigreringRapportDtoList.size,
            antallSedMottattHendelser
        )
        erKartleggingSedSendtPågående = false
    }

    fun hentStatus(): SedJournalføringMigreringRapportDto = SedJournalføringMigreringRapportDto(
        sedMottattMigreringRapportDtoList,
        sedSendtMigreringRapportDtoList,
        antallSedMottattHendelser,
        antallSedMottattSjekket
    )

    private fun kartleggForSedMottattHendelse(sedMottattHendelse: SedMottattHendelse) {
        val rinaSaksnummer = sedMottattHendelse.sedHendelse.rinaSakId
        val dokumentId = sedMottattHendelse.sedHendelse.rinaDokumentId
        antallSedMottattSjekket++

        val sedMedVedlegg = euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId)
        if (sedMedVedlegg.vedlegg!!.isNotEmpty()) {
            val journalpostId = sedMottattHendelse.journalpostId
            log.info("Fant vedlegg for sed med rinaSaksnummer {}, dokumentId {} og journalpostid {}", rinaSaksnummer, dokumentId, journalpostId)
            sedMottattMigreringRapportDtoList.add(SedMottattMigreringRapportDto(rinaSaksnummer, dokumentId, journalpostId))
        }
    }
}
