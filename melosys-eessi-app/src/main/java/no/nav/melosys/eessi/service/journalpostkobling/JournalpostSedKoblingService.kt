package no.nav.melosys.eessi.service.journalpostkobling

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.saf.SafConsumer
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import no.nav.melosys.eessi.models.JournalpostSedKobling
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

private val log = KotlinLogging.logger {}

@Service
class JournalpostSedKoblingService(
    private val journalpostSedKoblingRepository: JournalpostSedKoblingRepository,
    private val euxService: EuxService,
    private val saksrelasjonService: SaksrelasjonService,
    private val safConsumer: SafConsumer,
    private val melosysEessiMeldingMapperFactory: MelosysEessiMeldingMapperFactory
) {
    fun finnVedJournalpostID(journalpostID: String): Optional<JournalpostSedKobling> =
        journalpostSedKoblingRepository.findByJournalpostID(journalpostID)

    fun finnVedJournalpostIDOpprettMelosysEessiMelding(journalpostID: String): Optional<MelosysEessiMelding> {
        val journalpostSedKobling = journalpostSedKoblingRepository.findByJournalpostID(journalpostID).getOrNull()
        return Optional.ofNullable(
            journalpostSedKobling?.let { opprettEessiMelding(it) }
                ?: søkEtterRinaSaksnummerForJournalpost(journalpostID)?.let { rinaSaksnummer ->
                    opprettEessiMelding(rinaSaksnummer, journalpostID)
                }
        )
    }

    fun erASedAlleredeBehandlet(rinaSaksnummer: String): Boolean =
        journalpostSedKoblingRepository.findByRinaSaksnummer(rinaSaksnummer).any { it.erASed() }

    fun lagre(
        journalpostID: String,
        rinaSaksnummer: String,
        sedID: String,
        sedVersjon: String,
        bucType: String,
        sedType: String
    ): JournalpostSedKobling =
        journalpostSedKoblingRepository.save(JournalpostSedKobling(journalpostID, rinaSaksnummer, sedID, sedVersjon, bucType, sedType))

    private fun søkEtterRinaSaksnummerForJournalpost(journalpostID: String): String? =
        safConsumer.hentRinasakForJournalpost(journalpostID).getOrNull()?.also {
            log.info("Rinasaksnummer er null fra saf for journalpostId: {}", journalpostID)
        }

    private fun opprettEessiMelding(journalpostSedKobling: JournalpostSedKobling): MelosysEessiMelding {
        val buc = euxService.hentBuc(journalpostSedKobling.rinaSaksnummer)
        val organisation = buc.hentDokument(journalpostSedKobling.sedId).creator!!.organisation
        val sed = euxService.hentSed(journalpostSedKobling.rinaSaksnummer, journalpostSedKobling.sedId)
        val gsakSaksnummer = finnVedRinaSaksnummer(journalpostSedKobling)?.gsakSaksnummer
        return opprettMelosysEessiMelding(
            sed = sed!!,
            sedId = journalpostSedKobling.sedId,
            rinaSaksnummer = journalpostSedKobling.rinaSaksnummer,
            sedType = journalpostSedKobling.sedType,
            bucType = journalpostSedKobling.bucType,
            avsenderID = organisation!!.id,
            landkode = organisation.countryCode,
            journalpostID = journalpostSedKobling.journalpostID,
            saksnummer = gsakSaksnummer?.toString(),
            erEndring = journalpostSedKobling.sedVersjon.toInt() != 1,
            sedVersjon = journalpostSedKobling.sedVersjon
        ) ?: throw IllegalStateException("Kunne ikke opprette melding for journalpostSedKobling: $journalpostSedKobling")
    }

    private fun finnVedRinaSaksnummer(journalpostSedKobling: JournalpostSedKobling): FagsakRinasakKobling? =
        saksrelasjonService.finnVedRinaSaksnummer(journalpostSedKobling.rinaSaksnummer).getOrNull()

    private fun opprettEessiMelding(rinaSaksnummer: String, journalpostID: String): MelosysEessiMelding? {
        val buc = euxService.hentBuc(rinaSaksnummer)
        val documentOptional = buc.hentSistOppdaterteDocument() ?: run {
            log.warn("Finner ikke sist oppdaterte sed for rinasak {}", rinaSaksnummer)
            return null
        }
        val sedDocument = documentOptional
        val sedID = sedDocument.id
        val sedType = sedDocument.type
        val organisation = sedDocument.creator!!.organisation
        val sed = euxService.hentSed(rinaSaksnummer, sedID)
        val sedVersjon = "0" // har ikke sed-versjon
        return sed?.let {
            opprettMelosysEessiMelding(
                sed = sed,
                sedId = sedID,
                rinaSaksnummer = rinaSaksnummer,
                sedType = sedType,
                bucType = buc.bucType,
                avsenderID = organisation!!.id,
                landkode = organisation.countryCode,
                journalpostID = journalpostID,
                saksnummer = null,
                erEndring = false,
                sedVersjon = sedVersjon
            )
        } ?: throw IllegalStateException("Sed er null - Kunne ikke opprette melding for rinasak: $rinaSaksnummer")
    }

    private fun opprettMelosysEessiMelding(
        sed: SED,
        sedId: String?,
        rinaSaksnummer: String,
        sedType: String?,
        bucType: String?,
        avsenderID: String?,
        landkode: String?,
        journalpostID: String,
        saksnummer: String?,
        erEndring: Boolean,
        sedVersjon: String
    ): MelosysEessiMelding? {
        return melosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedType!!))
            .map(null, sed, sedId, rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, null, saksnummer, erEndring, sedVersjon)
    }
}
