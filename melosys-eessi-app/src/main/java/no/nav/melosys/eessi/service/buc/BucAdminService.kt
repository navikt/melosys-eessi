package no.nav.melosys.eessi.service.buc

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.eux.rina_api.EuxKotlinConsumer
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.ManglendeSed
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.RinaSakOversiktV3
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.SedAnalyseResult
import no.nav.melosys.eessi.integration.eux.rina_api.dto.v3.SedStatus
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger { }

@Service
class BucAdminService(
    private val euxKotlinConsumer: EuxKotlinConsumer,
    private val sedMottattHendelseRepository: SedMottattHendelseRepository
) {

    fun analyserManglendeSeder(rinaSaksnummer: String): SedAnalyseResult {
        log.info { "Analyserer SED-status for sak $rinaSaksnummer" }
        val oversikt = euxKotlinConsumer.hentBucOversiktV3(rinaSaksnummer)
        val manglendeSeder = analyserSedStatus(oversikt)

        return SedAnalyseResult(
            rinaSaksnummer = rinaSaksnummer,
            manglendeSeder = manglendeSeder,
            oversikt = oversikt
        )
    }

    private fun analyserSedStatus(oversikt: RinaSakOversiktV3): List<ManglendeSed> {
        val rinaSaksnummer = oversikt.sakId ?: return emptyList()

        // Map SED ID -> publisert status (null = mangler lokalt)
        val lokaleSedMap = sedMottattHendelseRepository
            .findAllByRinaSaksnummerSortedByMottattDatoDesc(rinaSaksnummer)
            .mapNotNull { it.sedHendelse.rinaDokumentId?.let { id -> id to it.publisertKafka } }
            .toMap()

        log.info { "Fant ${oversikt.sedListe?.size ?: 0} SED-er i RINA og ${lokaleSedMap.size} lokalt for sak $rinaSaksnummer" }

        return oversikt.sedListe
            ?.mapNotNull { sed ->
                val sedId = sed.sedId ?: return@mapNotNull null
                val publisertKafka = lokaleSedMap[sedId]

                when {
                    publisertKafka == null -> ManglendeSed(
                        sedType = sed.sedType ?: "UKJENT",
                        status = SedStatus.MANGLER_LOKALT,
                        sedId = sedId,
                        beskrivelse = "SED finnes i RINA med status '${sed.status}', men ikke i lokal database."
                    )
                    !publisertKafka -> ManglendeSed(
                        sedType = sed.sedType ?: "UKJENT",
                        status = SedStatus.IKKE_PUBLISERT,
                        sedId = sedId,
                        beskrivelse = "SED finnes lokalt, men er ikke publisert til Kafka."
                    )
                    else -> ManglendeSed(
                        sedType = sed.sedType ?: "UKJENT",
                        status = SedStatus.FINNES_LOKALT,
                        sedId = sedId,
                        beskrivelse = "SED finnes lokalt og er publisert til Kafka."
                    )
                }
            }
            ?: emptyList()
    }

    fun hentRinaOversikt(rinaSaksnummer: String): RinaSakOversiktV3 {
        return euxKotlinConsumer.hentBucOversiktV3(rinaSaksnummer)
    }

    fun resendSed(rinaSaksnummer: String, dokumentId: String) {
        log.info { "Ber om gjensending av SED $dokumentId for sak $rinaSaksnummer" }
        euxKotlinConsumer.resendSed(rinaSaksnummer, dokumentId)
    }

    fun resendSedListe(sedIds: List<String>) {
        log.info { "Ber om gjensending av ${sedIds.size} SED-er" }
        euxKotlinConsumer.resendSedListe(sedIds)
        log.info { "Gjensending av ${sedIds.size} SED-er fullført" }
    }
}
