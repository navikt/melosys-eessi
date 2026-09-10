package no.nav.melosys.eessi.service.mottak

import mu.KotlinLogging
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Manuelle inngrep i SED-mottak, kalt fra admin-endepunkter. Holdes atskilt fra [SedMottakService]
 * slik at den ordinære mottaksflyten ikke eksponerer operasjoner den selv ikke bruker.
 */
@Service
class SedMottakAdminService(
    private val euxService: EuxService,
    private val sedMottattHendelseRepository: SedMottattHendelseRepository,
    private val identifiseringsoppgaveService: IdentifiseringsoppgaveService
) {

    @Transactional
    fun opprettIdentifiseringsoppgaveForUpublisertASed(rinaSaksnummer: String): IdentifiseringsoppgaveResultat {
        val hendelserPåSak = sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(rinaSaksnummer)

        if (hendelserPåSak.isEmpty()) {
            throw NotFoundException("Fant ingen mottatte SED-hendelser for rinasak $rinaSaksnummer")
        }

        val aSed = hendelserPåSak.firstOrNull { it.sedHendelse.erASED() }
            ?: throw NotFoundException("Fant ingen A-SED for rinasak $rinaSaksnummer")

        // Publisert betyr identifisert og sendt til Melosys. Ny oppgave ville gitt dobbeltbehandling.
        if (aSed.publisertKafka) {
            throw ValidationException(
                "A-SED ${aSed.sedHendelse.sedId} i rinasak $rinaSaksnummer er allerede publisert på Kafka, " +
                    "altså identifisert og sendt videre til Melosys. Oppretter ikke identifiseringsoppgave."
            )
        }

        val tidligereOppgaver = identifiseringsoppgaveService.kartleggTidligereOppgaver(rinaSaksnummer)
        tidligereOppgaver.firstOrNull { it.erÅpen }?.let {
            throw ValidationException(
                "Det finnes allerede en åpen identifiseringsoppgave ${it.oppgaveId} (status ${it.status}) " +
                    "for rinasak $rinaSaksnummer. Oppretter ikke ny."
            )
        }

        val sed = euxService.hentSedMedRetry(aSed.sedHendelse.rinaSakId, aSed.sedHendelse.rinaDokumentId)

        log.info {
            "Admin: oppretter oppgave til ID og fordeling for A-SED ${aSed.sedHendelse.sedId} i rinasak $rinaSaksnummer. " +
                "Tidligere oppgaver på saken: ${tidligereOppgaver.ifEmpty { "ingen" }}"
        }
        val oppgaveId = identifiseringsoppgaveService.opprettOgLagreOppgave(aSed, sed)

        return IdentifiseringsoppgaveResultat(
            rinaSaksnummer = rinaSaksnummer,
            sedId = aSed.sedHendelse.sedId,
            sedType = aSed.sedHendelse.sedType,
            journalpostId = aSed.journalpostId,
            oppgaveId = oppgaveId,
            tidligereOppgaver = tidligereOppgaver
        )
    }

    data class IdentifiseringsoppgaveResultat(
        val rinaSaksnummer: String,
        val sedId: String,
        val sedType: String,
        val journalpostId: String?,
        val oppgaveId: String,
        val tidligereOppgaver: List<IdentifiseringsoppgaveService.TidligereOppgave>
    )
}
