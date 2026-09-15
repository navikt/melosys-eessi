package no.nav.melosys.eessi.service.mottak

import mu.KotlinLogging
import no.nav.melosys.eessi.identifisering.BucIdentifisertService
import no.nav.melosys.eessi.identifisering.PersonIdentifisering
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.BucType.Companion.erHBucsomSkalKonsumeres
import no.nav.melosys.eessi.models.SedMottattHendelse
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.Participant
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService
import no.nav.melosys.eessi.service.mottak.SedA003UnntaksreglerForTredjelandsborgere.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class SedMottakService(
    private val euxService: EuxService,
    private val sedMottattHendelseRepository: SedMottattHendelseRepository,
    private val journalpostSedKoblingService: JournalpostSedKoblingService,
    private val sedMetrikker: SedMetrikker,
    private val personIdentifisering: PersonIdentifisering,
    private val bucIdentifisertService: BucIdentifisertService,
    private val saksrelasjonService: SaksrelasjonService,
    private val sedLagerService: SedLagerService,
    private val identifiseringsoppgaveService: IdentifiseringsoppgaveService,
    @Value("\${rina.institusjon-id}") private val rinaInstitusjonsId: String
) {

    @Transactional
    fun behandleSedMottakHendelse(sedMottattHendelse: SedMottattHendelse) {
        val sedHendelse = sedMottattHendelse.sedHendelse
        log.info("Behandler mottatt SED ${sedHendelse.sedId} av type ${sedHendelse.sedType} i rinaSak ${sedHendelse.rinaSakId}")

        if (sedHendelse.erIkkeLaBuc() && !erHBucFraMelosys(sedMottattHendelse)) {
            log.debug("Ignorerer mottatt SED ${sedHendelse.sedId} BUC type ikke tilknyttet melosys")
            return
        }

        if (sedHendelse.erX100()) {
            log.info("Ignorerer mottatt SED ${sedHendelse.sedId} av typen X100")
            return
        }

        if (sedMottattHendelseRepository.findBySedID(sedHendelse.sedId).isPresent) {
            log.info("Mottatt SED ${sedHendelse.sedId} er allerede behandlet")
            return
        }

        val erXSedBehandletUtenASedEllerHSed = erXSedBehandletUtenASedEllerHSed(sedHendelse)

        check(!erXSedBehandletUtenASedEllerHSed) {
            "Mottatt SED ${sedHendelse.sedId} av type ${sedHendelse.sedType} har ikke tilhørende A sed behandlet"
        }

        sjekkSedMottakerOgAvsenderID(sedHendelse)
        sjekkSedMottakerOgAvsenderNavn(sedHendelse)

        if (SedType.valueOf(sedMottattHendelse.sedHendelse.sedType).erXSED()) {
            val initiellSed = sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(
                sedMottattHendelse.sedHendelse.rinaSakId
            ).lastOrNull {
                it.sedHendelse.erASED() || it.sedHendelse.erHSED()
            }

            if (initiellSed != null && !initiellSed.skalJournalfoeres) {
                sedMottattHendelse.skalJournalfoeres = false
                sedMottattHendelseRepository.save(sedMottattHendelse)
                return
            }
        }

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

    private fun erXSedBehandletUtenASedEllerHSed(sedHendelse: SedHendelse): Boolean {
        if (!sedHendelse.erXSedSomTrengerKontroll()) return false

        log.info("X-SED ${sedHendelse.sedId} av type ${sedHendelse.sedType} trenger kontroll, sjekker om tilhørende A-SED/H-SED finnes for rinaSakId ${sedHendelse.rinaSakId}")

        if (sedHendelse.sedType == SedType.X007.name) {
            val buc = euxService.hentBuc(sedHendelse.rinaSakId)

            val sedTypeErX007OgNorgeErSakseier = buc.participants.any { p ->
                p.role == Participant.ParticipantRole.SAKSEIER
                    && p.organisation!!.id == rinaInstitusjonsId
            }

            if (sedTypeErX007OgNorgeErSakseier) {
                log.info("X007 ${sedHendelse.sedId}: Norge er sakseier, hopper over A-SED/H-SED-sjekk")
                return false
            }
        }

        val sedHendelser = sedMottattHendelseRepository.findAllByRinaSaksnummerSortedByMottattDatoDesc(sedHendelse.rinaSakId)
        val harASedEllerHSedIMottattHendelse = sedHendelser.any {
            it.sedHendelse.erASED() || it.sedHendelse.erHSED()
        }

        if (harASedEllerHSedIMottattHendelse) {
            log.info("Fant tilhørende A-SED/H-SED i sed_mottatt_hendelse for rinaSakId ${sedHendelse.rinaSakId}")
            return false
        }

        log.info("Fant ingen tilhørende A-SED/H-SED i sed_mottatt_hendelse for rinaSakId ${sedHendelse.rinaSakId}. Sjekker journalpost_sed_kobling.")

        if (harASedEllerHSedIJournalpostKobling(sedHendelse)) return false

        log.warn("X-SED ${sedHendelse.sedId} av type ${sedHendelse.sedType}: ingen tilhørende A-SED/H-SED funnet for rinaSakId ${sedHendelse.rinaSakId}")
        return true
    }

    private fun harASedEllerHSedIJournalpostKobling(sedHendelse: SedHendelse): Boolean {
        if (sedHendelse.sedType != SedType.X001.name) return false

        log.info("X001 ${sedHendelse.sedId}: sjekker fallback mot journalpost_sed_kobling for rinaSakId ${sedHendelse.rinaSakId}")
        val funnet = journalpostSedKoblingService.harASedEllerHSedForRinaSak(sedHendelse.rinaSakId)
        if (funnet) {
            log.info("X001 ${sedHendelse.sedId}: fant tilhørende A-SED/H-SED i journalpost_sed_kobling (fallback) for rinaSakId ${sedHendelse.rinaSakId}")
        } else {
            log.info("X001 ${sedHendelse.sedId}: fant ingen tilhørende A-SED/H-SED i journalpost_sed_kobling for rinaSakId ${sedHendelse.rinaSakId}")
        }
        return funnet
    }

    private fun opprettOppgaveIdentifisering(sedMottatt: SedMottattHendelse, sed: SED) {
        if (!sedMottatt.sedHendelse.erASED()) {
            log.info("SED er ikke A-sed, oppretter ikke oppgave til ID og fordeling, SED: ${sedMottatt.sedHendelse.sedId}")
            return
        }

        fun hentAvsenderLand(): String = euxService.hentBuc(sedMottatt.sedHendelse.rinaSakId).hentAvsenderLand()
        if (sed.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted(::hentAvsenderLand)) {
            sedMottatt.skalJournalfoeres = false
            sedMottattHendelseRepository.save(sedMottatt)
            lagreSed(sedMottatt, sed)
            log.info("SED er A003 og tredjelandsborger uten arbeidssted i Norge, oppretter ikke oppgave til ID og fordeling, SED: ${sedMottatt.sedHendelse.sedId}")
            return
        }

        log.info("Oppretter oppgave til ID og fordeling for SED ${sedMottatt.sedHendelse.sedId}")

        val rinaSaksnummer = sedMottatt.sedHendelse.rinaSakId
        val åpenOppgave = identifiseringsoppgaveService.finnÅpenOppgave(rinaSaksnummer)
        if (åpenOppgave != null) {
            log.info("Identifiseringsoppgave ${åpenOppgave.oppgaveId} finnes allerede for rinasak $rinaSaksnummer")
            return
        }
        identifiseringsoppgaveService.opprettOgLagreOppgave(sedMottatt, sed)
    }

    private fun lagreSed(sedMottatt: SedMottattHendelse, sed: SED) {
        try {
            // Dette må gjøres i en separat transaksjon for å unngå at eksisterende transaksjon blir rullet tilbake
            sedLagerService.lagreSedSeparatTransaksjon(sedMottatt, sed)
        } catch (e: Exception) {
            log.error("Kunne ikke lagre SED ${sedMottatt.sedHendelse.sedId} i sed mottatt lager for tredjelandsborger uten arbeidssted i Norge", e)
        }
    }

    private fun erHBucFraMelosys(sedMottattHendelse: SedMottattHendelse): Boolean =
        erHBucsomSkalKonsumeres(sedMottattHendelse.sedHendelse.bucType)
            && harEksisterendeSaksRelasjon(sedMottattHendelse.sedHendelse.rinaSakId)


    private fun harEksisterendeSaksRelasjon(rinaSakId: String): Boolean =
        saksrelasjonService.finnVedRinaSaksnummer(rinaSakId).isPresent()
}
