package no.nav.melosys.eessi.service.mottak

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.PersonFasade
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.IdentRekvisisjonTilMellomlagringMapper
import no.nav.melosys.eessi.models.BucIdentifiseringOppg
import no.nav.melosys.eessi.models.SedMottattHendelse
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.journalfoering.OpprettInngaaendeJournalpostService
import no.nav.melosys.eessi.service.oppgave.OppgaveService
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

/**
 * Oppretting og oppslag av identifiseringsoppgaver til ID og fordeling. Deles av ordinær SED-mottak
 * og av admin, slik at begge veier gir samme journalpost, oppgave og kobling.
 */
@Service
class IdentifiseringsoppgaveService(
    private val euxService: EuxService,
    private val personFasade: PersonFasade,
    private val opprettInngaaendeJournalpostService: OpprettInngaaendeJournalpostService,
    private val oppgaveService: OppgaveService,
    private val sedMottattHendelseRepository: SedMottattHendelseRepository,
    private val bucIdentifiseringOppgRepository: BucIdentifiseringOppgRepository
) {

    /**
     * En oppgave Oppgave svarer 404 på rapporteres som [STATUS_FINNES_IKKE] og regnes ikke som åpen. Uten
     * dette ville en død oppgave-id blokkert ny oppgave og veltet SED-mottak for hele rinasaken.
     */
    fun kartleggTidligereOppgaver(rinaSaksnummer: String): List<TidligereOppgave> =
        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer).map { kobling ->
            try {
                val oppgave = oppgaveService.hentOppgave(kobling.oppgaveId)
                TidligereOppgave(kobling.oppgaveId, oppgave.status ?: STATUS_UKJENT, oppgave.erÅpen())
            } catch (e: NotFoundException) {
                log.warn(e) { "Oppgave ${kobling.oppgaveId} på rinasak $rinaSaksnummer finnes ikke i Oppgave" }
                TidligereOppgave(kobling.oppgaveId, STATUS_FINNES_IKKE, false)
            }
        }

    fun finnÅpenOppgave(rinaSaksnummer: String): TidligereOppgave? =
        kartleggTidligereOppgaver(rinaSaksnummer).firstOrNull { it.erÅpen }

    fun opprettOgLagreOppgave(sedMottattHendelse: SedMottattHendelse, sed: SED): String {
        val journalpostID = sedMottattHendelse.journalpostId
            ?.also { log.info("Gjenbruker eksisterende journalpost $it for SED ${sedMottattHendelse.sedHendelse.sedId}") }
            ?: opprettJournalpost(sedMottattHendelse)
        val oppgaveID = opprettOppgave(sedMottattHendelse, sed, journalpostID)

        bucIdentifiseringOppgRepository.save(
            BucIdentifiseringOppg.builder()
                .rinaSaksnummer(sedMottattHendelse.sedHendelse.rinaSakId)
                .oppgaveId(oppgaveID)
                .versjon(1)
                .build()
        )

        log.info("Opprettet oppgave med id $oppgaveID")
        return oppgaveID
    }

    private fun opprettOppgave(sedMottattHendelse: SedMottattHendelse, sed: SED, journalpostID: String): String {
        val personFraSed = sed.finnPerson().orElse(null)

        return when {
            personFraSed != null && !personFraSed.harNorskPersonnummer() -> {
                val identRekvisjonTilMellomlagring =
                    IdentRekvisisjonTilMellomlagringMapper.byggIdentRekvisisjonTilMellomlagring(sedMottattHendelse, sed)

                val lenkeForRekvirering = personFasade.opprettLenkeForRekvirering(identRekvisjonTilMellomlagring)

                oppgaveService.opprettOppgaveTilIdOgFordeling(
                    journalpostID,
                    sedMottattHendelse.sedHendelse.sedType,
                    sedMottattHendelse.sedHendelse.rinaSakId,
                    lenkeForRekvirering
                )
            }

            else -> {
                oppgaveService.opprettOppgaveTilIdOgFordeling(
                    journalpostID,
                    sedMottattHendelse.sedHendelse.sedType,
                    sedMottattHendelse.sedHendelse.rinaSakId
                )
            }
        }
    }

    private fun opprettJournalpost(sedMottattHendelse: SedMottattHendelse): String {
        log.info("Oppretter journalpost for SED ${sedMottattHendelse.sedHendelse.rinaDokumentId}")
        val sedMedVedlegg = euxService.hentSedMedVedlegg(
            sedMottattHendelse.sedHendelse.rinaSakId, sedMottattHendelse.sedHendelse.rinaDokumentId
        )

        val journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
            sedMottattHendelse.sedHendelse, sedMedVedlegg, null
        )

        sedMottattHendelse.journalpostId = journalpostID
        sedMottattHendelseRepository.save(sedMottattHendelse)
        return journalpostID
    }

    data class TidligereOppgave(
        val oppgaveId: String,
        val status: String,
        val erÅpen: Boolean
    )

    companion object {
        const val STATUS_FINNES_IKKE = "FINNES_IKKE"
        const val STATUS_UKJENT = "UKJENT"
    }
}
