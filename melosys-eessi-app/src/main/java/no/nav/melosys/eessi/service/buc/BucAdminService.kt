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
        log.info { "Analyserer manglende SEDer for sak $rinaSaksnummer" }
        val oversikt = euxKotlinConsumer.hentBucOversiktV3(rinaSaksnummer)
        val manglendeSeder = identifiserManglendeSeder(oversikt)

        return SedAnalyseResult(
            rinaSaksnummer = rinaSaksnummer,
            manglendeSeder = manglendeSeder,
            oversikt = oversikt
        )
    }

    private fun identifiserManglendeSeder(oversikt: RinaSakOversiktV3): List<ManglendeSed> {
        val rinaSaksnummer = oversikt.sakId ?: return emptyList()
        val lokaleSedIder = sedMottattHendelseRepository
            .findAllByRinaSaksnummerAndPublisertKafkaSortedByMottattDato(rinaSaksnummer, true)
            .mapNotNull { it.sedHendelse?.sedId }
            .toSet()

        log.info { "Fant ${oversikt.sedListe?.size ?: 0} SEDer i RINA og ${lokaleSedIder.size} lokalt for sak $rinaSaksnummer" }

        return oversikt.sedListe
            ?.filterNot { sed -> lokaleSedIder.contains(sed.sedId) }
            ?.map { sed ->
                ManglendeSed(
                    sedType = sed.sedType ?: "UKJENT",
                    status = SedStatus.MANGLER_LOKALT,
                    dokumentId = sed.sedId,
                    beskrivelse = "SED finnes i RINA med status '${sed.status}', men ikke i lokal database."
                )
            }
            ?: emptyList()
    }

    fun hentRinaOversikt(rinaSaksnummer: String): RinaSakOversiktV3 {
        return euxKotlinConsumer.hentBucOversiktV3(rinaSaksnummer)
    }

    fun resendSed(rinaSaksnummer: String, dokumentId: String) {
        log.info { "Ber om gjensending av SED $dokumentId for sak $rinaSaksnummer" }
        try {
            euxKotlinConsumer.resendSed(rinaSaksnummer, dokumentId)
        } catch (e: Exception) {
            log.error(e) { "Feil ved gjensending av SED $dokumentId for sak $rinaSaksnummer" }
            throw e
        }
    }
}
