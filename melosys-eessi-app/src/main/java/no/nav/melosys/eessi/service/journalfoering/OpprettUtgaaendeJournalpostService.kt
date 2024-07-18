package no.nav.melosys.eessi.service.journalfoering

import mu.KotlinLogging
import no.nav.melosys.eessi.identifisering.PersonIdentifisering
import no.nav.melosys.eessi.integration.PersonFasade
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException
import no.nav.melosys.eessi.integration.sak.Sak
import no.nav.melosys.eessi.kafka.consumers.SedHendelse
import no.nav.melosys.eessi.metrikker.SedMetrikker
import no.nav.melosys.eessi.models.SedSendtHendelse
import no.nav.melosys.eessi.repository.SedSendtHendelseRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.oppgave.OppgaveService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class OpprettUtgaaendeJournalpostService(
    private val saksrelasjonService: SaksrelasjonService,
    private val journalpostService: JournalpostService,
    private val euxService: EuxService,
    private val personFasade: PersonFasade,
    private val oppgaveService: OppgaveService,
    private val sedMetrikker: SedMetrikker,
    private val personIdentifisering: PersonIdentifisering,
    private val sedSendtHendelseRepository: SedSendtHendelseRepository
) {
    fun behandleSedSendtHendelse(sedSendt: SedHendelse) {
        try {
            if (sedInneholderPersonId(sedSendt)) {
                val journalpostId = arkiverUtgaaendeSed(sedSendt)
                log.info("Journalpost opprettet med id: {}", journalpostId)
                journalfoerTidligereSedDersomEksisterer(sedSendt.rinaSakId)
            } else {
                log.info("SED {} inneholder ikke personId, journalfører ikke.", sedSendt.rinaDokumentId)
                sedSendtHendelseRepository.save(SedSendtHendelse(sedSendt.id, sedSendt, null))
            }
            sedMetrikker.sedSendt(sedSendt.sedType)
        } catch (e: SedAlleredeJournalførtException) {
            log.info("SED {} allerede journalført", e.sedID)
        }
    }

    fun journalfoerTidligereSedDersomEksisterer(rinaSakId: String) {
        val sedSendtHendelser: List<SedSendtHendelse> = sedSendtHendelseRepository.findAllByRinaSaksnummerAndAndJournalpostIdIsNull(
            rinaSakId
        )
        for (sedSendtHendelse in sedSendtHendelser) {
            arkiverUtgaaendeSed(sedSendtHendelse.sedHendelse)
            sedSendtHendelseRepository.delete(sedSendtHendelse)
        }
        log.info("Journalført {} tidligere utgående SEDer", sedSendtHendelser.size)
    }

    private fun sedInneholderPersonId(sedHendelse: SedHendelse): Boolean {
        val rinaSaksId = sedHendelse.rinaSakId
        val sed = euxService.hentSedMedRetry(rinaSaksId, sedHendelse.rinaDokumentId)
        log.info("Søker etter person for SED")
        val person = personIdentifisering.identifiserPerson(rinaSaksId, sed)
        return person.isPresent
    }

    fun arkiverUtgaaendeSed(sedSendt: SedHendelse): String {
        val sak = saksrelasjonService.finnArkivsakForRinaSaksnummer(sedSendt.rinaSakId)
        if (sak.isEmpty) {
            return arkiverUtenSak(sedSendt)
        }
        return arkiverMedSak(sedSendt, sak.get())
    }

    private fun arkiverMedSak(sedSendt: SedHendelse, sak: Sak): String {
        log.info("Journalfører dokument: {}", sedSendt.rinaDokumentId)
        val navIdent = personFasade.hentNorskIdent(sak.aktoerId)
        val response = opprettUtgåendeJournalpost(sedSendt, sak, navIdent)
        if (!"ENDELIG".equals(response.journalstatus, ignoreCase = true)) {
            log.info("Journalpost {} ble ikke endelig journalført. Melding: {}", response.journalpostId, response.melding)
            opprettUtgåendeJournalføringsoppgave(sedSendt, response.journalpostId, personFasade.hentAktoerId(navIdent))
        }
        return response.journalpostId
    }

    private fun arkiverUtenSak(sedSendt: SedHendelse): String {
        log.info("Journalfører dokument uten sakstilknytning: {}", sedSendt.rinaDokumentId)
        val navIdent = sedSendt.navBruker
        val response = opprettUtgåendeJournalpost(sedSendt, null, navIdent)
        opprettUtgåendeJournalføringsoppgave(sedSendt, response.journalpostId, navIdent)
        return response.journalpostId
    }

    private fun opprettUtgåendeJournalpost(sedSendt: SedHendelse, sak: Sak?, navIdent: String?): OpprettJournalpostResponse {
        return journalpostService.opprettUtgaaendeJournalpost(
            sedSendt,
            sak,
            euxService.hentSedMedVedlegg(sedSendt.rinaSakId, sedSendt.rinaDokumentId),
            navIdent
        )
    }

    private fun opprettUtgåendeJournalføringsoppgave(sedSendt: SedHendelse, journalpostId: String, navIdent: String): String? {
        return oppgaveService.opprettUtgåendeJfrOppgave(
            journalpostId,
            sedSendt,
            if (StringUtils.isNotEmpty(navIdent)) personFasade.hentAktoerId(navIdent) else null,
            euxService.hentRinaUrl(sedSendt.rinaSakId)
        )
    }
}
