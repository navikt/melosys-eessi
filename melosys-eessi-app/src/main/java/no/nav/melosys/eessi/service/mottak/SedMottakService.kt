package no.nav.melosys.eessi.service.mottak

import io.getunleash.Unleash
import jakarta.transaction.Transactional
import mu.KotlinLogging
import no.nav.melosys.eessi.config.featuretoggle.ToggleName.TREDJELANDSBORGER_UTEN_NORGE_SOM_ARBEIDSSTED
import no.nav.melosys.eessi.identifisering.BucIdentifisertService
import no.nav.melosys.eessi.identifisering.FnrUtils
import no.nav.melosys.eessi.identifisering.PersonIdentifisering
import no.nav.melosys.eessi.integration.PersonFasade
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.IdentRekvisisjonTilMellomlagringMapper
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.BucIdentifiseringOppg
import no.nav.melosys.eessi.models.BucType.Companion.erHBucsomSkalKonsumeres
import no.nav.melosys.eessi.models.SedMottattHendelse
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.Participant
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.nav.Person
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.journalfoering.OpprettInngaaendeJournalpostService
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService
import no.nav.melosys.eessi.service.oppgave.OppgaveService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class SedMottakService(
    private val euxService: EuxService,
    private val personFasade: PersonFasade,
    private val opprettInngaaendeJournalpostService: OpprettInngaaendeJournalpostService,
    private val oppgaveService: OppgaveService,
    private val sedMottattHendelseRepository: SedMottattHendelseRepository,
    private val bucIdentifiseringOppgRepository: BucIdentifiseringOppgRepository,
    private val journalpostSedKoblingService: JournalpostSedKoblingService,
    private val sedMetrikker: SedMetrikker,
    private val personIdentifisering: PersonIdentifisering,
    private val bucIdentifisertService: BucIdentifisertService,
    private val saksrelasjonService: SaksrelasjonService,
    private val unleach: Unleash,
    @Value("\${rina.institusjon-id}") private val rinaInstitusjonsId: String
) {

    @Transactional
    fun behandleSedMottakHendelse(sedMottattHendelse: SedMottattHendelse) {
        if (sedMottattHendelse.sedHendelse.erIkkeLaBuc() && !erHBucFraMelosys(sedMottattHendelse)) {
            log.debug("Ignorerer mottatt SED ${sedMottattHendelse.sedHendelse.sedId} BUC type ikke tilknyttet melosys")
            return
        }

        if (sedMottattHendelse.sedHendelse.erX100()) {
            log.info("Ignorerer mottatt SED ${sedMottattHendelse.sedHendelse.sedId} av typen X100")
            return
        }

        if (sedMottattHendelseRepository.findBySedID(sedMottattHendelse.sedHendelse.sedId).isPresent) {
            log.info("Mottatt SED ${sedMottattHendelse.sedHendelse.sedId} er allerede behandlet")
            return
        }

        check(!erXSedBehandletUtenASed(sedMottattHendelse.sedHendelse)) {
            "Mottatt SED ${sedMottattHendelse.sedHendelse.sedId} av type ${
                sedMottattHendelse.sedHendelse.sedType
            } har ikke tilhørende A sed behandlet"
        }

        sjekkSedMottakerOgAvsenderID(sedMottattHendelse.sedHendelse)
        sjekkSedMottakerOgAvsenderNavn(sedMottattHendelse.sedHendelse)

        val lagretHendelse = sedMottattHendelseRepository.save(sedMottattHendelse)

        val sed = euxService.hentSedMedRetry(
            sedMottattHendelse.sedHendelse.rinaSakId,
            sedMottattHendelse.sedHendelse.rinaDokumentId
        )

        log.info("Søker etter person for SED")
        personIdentifisering.identifiserPerson(lagretHendelse.sedHendelse.rinaSakId, sed)
            .ifPresentOrElse(
                { ident ->
                    bucIdentifisertService.lagreIdentifisertPerson(
                        lagretHendelse.sedHendelse.rinaSakId,
                        ident
                    )
                },
                { opprettOppgaveIdentifisering(lagretHendelse, sed) }
            )

        sedMetrikker.sedMottatt(sedMottattHendelse.sedHendelse.sedType)
    }

    private fun sjekkSedMottakerOgAvsenderID(sedHendelse: SedHendelse) {
        sedHendelse.run {
            when {
                avsenderId.isNullOrEmpty() && mottakerId.isNullOrEmpty() ->
                    error("Mottatt SED $sedId mangler avsenderId og mottakerId")

                mottakerId.isNullOrEmpty() ->
                    error("Mottatt SED $sedId mangler mottakerId")

                avsenderId.isNullOrEmpty() ->
                    error("Mottatt SED $sedId mangler avsenderId")
            }
        }
    }

    private fun sjekkSedMottakerOgAvsenderNavn(sedHendelse: SedHendelse) {
        sedHendelse.run {
            when {
                avsenderNavn.isNullOrEmpty() && mottakerNavn.isNullOrEmpty() ->
                    error("Mottatt SED $sedId mangler avsenderNavn og mottakerNavn")

                mottakerNavn.isNullOrEmpty() ->
                    error("Mottatt SED $sedId mangler mottakerNavn")

                avsenderNavn.isNullOrEmpty() ->
                    error("Mottatt SED $sedId mangler avsenderNavn")
            }
        }
    }

    private fun erXSedBehandletUtenASed(sedHendelse: SedHendelse): Boolean {
        if (!sedHendelse.erXSedSomTrengerKontroll()) return false

        if (sedHendelse.sedType == SedType.X007.name) {
            val buc = euxService.hentBuc(sedHendelse.rinaSakId)

            val sedTypeErX007OgNorgeErSakseier = buc.participants.any { p ->
                p.role == Participant.ParticipantRole.SAKSEIER
                    && p.organisation!!.id == rinaInstitusjonsId
            }

            if (sedTypeErX007OgNorgeErSakseier) return false
        }

        return !journalpostSedKoblingService.erASedAlleredeBehandlet(sedHendelse.rinaSakId)
    }

    private fun opprettOppgaveIdentifisering(sedMottatt: SedMottattHendelse, sed: SED) {
        if (!sedMottatt.sedHendelse.erASED()) {
            log.info("SED er ikke A-sed, oppretter ikke oppgave til ID og fordeling, SED: ${sedMottatt.sedHendelse.sedId}")
            return
        }

        if (unleach.isEnabled(TREDJELANDSBORGER_UTEN_NORGE_SOM_ARBEIDSSTED)) {
            fun hentAvsenderLand(): String = euxService.hentBuc(sedMottatt.sedHendelse.rinaSakId).hentAvsenderLand()
            if (sed.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted(::hentAvsenderLand)) {
                log.info("SED er A003 og tredjelandsborger uten arbeidssted i Norge, oppretter ikke oppgave til ID og fordeling, SED: ${sedMottatt.sedHendelse.sedId}")
                return
            }
        }

        log.info("Oppretter oppgave til ID og fordeling for SED ${sedMottatt.sedHendelse.sedId}")

        val rinaSaksnummer = sedMottatt.sedHendelse.rinaSakId
        bucIdentifiseringOppgRepository.findByRinaSaksnummer(rinaSaksnummer)
            .firstOrNull { this.oppgaveErÅpen(it) }
            ?.let { log.info("Identifiseringsoppgave ${it.oppgaveId} finnes allerede for rinasak $rinaSaksnummer") }
            ?: opprettOgLagreIdentifiseringsoppgave(sedMottatt, sed)
    }

    private fun oppgaveErÅpen(bucIdentifiseringOppg: BucIdentifiseringOppg): Boolean =
        oppgaveService.hentOppgave(bucIdentifiseringOppg.oppgaveId).erÅpen()

    private fun opprettOgLagreIdentifiseringsoppgave(sedMottattHendelse: SedMottattHendelse, sed: SED) {
        val journalpostID = opprettJournalpost(sedMottattHendelse)
        val oppgaveID = opprettOgLagreIndentifiseringsoppgave(sedMottattHendelse, sed, journalpostID)

        bucIdentifiseringOppgRepository.save(
            BucIdentifiseringOppg.builder()
                .rinaSaksnummer(sedMottattHendelse.sedHendelse.rinaSakId)
                .oppgaveId(oppgaveID)
                .versjon(1)
                .build()
        )

        log.info("Opprettet oppgave med id $oppgaveID")
    }

    private fun opprettOgLagreIndentifiseringsoppgave(
        sedMottattHendelse: SedMottattHendelse,
        sed: SED,
        journalpostID: String
    ): String {
        val personFraSed = sed.finnPerson().orElse(null)

        return when {
            personFraSed != null && !harNorskPersonnummer(personFraSed) -> {
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

    private fun harNorskPersonnummer(personFraSed: Person): Boolean = personFraSed.finnNorskPin()
        .map { it.identifikator }
        .flatMap(FnrUtils::filtrerUtGyldigNorskIdent)
        .isPresent

    private fun opprettJournalpost(sedMottattHendelse: SedMottattHendelse, navIdent: String? = null): String {
        log.info("Oppretter journalpost for SED ${sedMottattHendelse.sedHendelse.rinaDokumentId}")
        val sedMedVedlegg = euxService.hentSedMedVedlegg(
            sedMottattHendelse.sedHendelse.rinaSakId, sedMottattHendelse.sedHendelse.rinaDokumentId
        )

        val journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(
            sedMottattHendelse.sedHendelse, sedMedVedlegg, navIdent
        )

        sedMottattHendelse.journalpostId = journalpostID
        sedMottattHendelseRepository.save(sedMottattHendelse)
        return journalpostID
    }

    private fun erHBucFraMelosys(sedMottattHendelse: SedMottattHendelse): Boolean =
        erHBucsomSkalKonsumeres(sedMottattHendelse.sedHendelse.bucType)
            && harEksisterendeSaksRelasjon(sedMottattHendelse.sedHendelse.rinaSakId)


    private fun harEksisterendeSaksRelasjon(rinaSakId: String): Boolean =
        saksrelasjonService.finnVedRinaSaksnummer(rinaSakId).isPresent()
}
