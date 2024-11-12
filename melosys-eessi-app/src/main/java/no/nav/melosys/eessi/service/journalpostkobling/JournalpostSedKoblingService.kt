package no.nav.melosys.eessi.service.journalpostkobling

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.saf.SafConsumer
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import no.nav.melosys.eessi.models.JournalpostSedKobling
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.EessiMeldingQuery
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
        val organisation = buc.hentDokument(journalpostSedKobling.sedId).creator?.organisation
            ?: throw IllegalStateException("Organisation er null")
        val sed = hentSed(journalpostSedKobling.rinaSaksnummer, journalpostSedKobling.sedId)
        val gsakSaksnummer = finnVedRinaSaksnummer(journalpostSedKobling)?.gsakSaksnummer
        return opprettMelosysEessiMelding(
            EessiMeldingQuery(
                sed = sed,
                rinaDokumentID = journalpostSedKobling.sedId,
                rinaSaksnummer = journalpostSedKobling.rinaSaksnummer,
                sedType = journalpostSedKobling.sedType,
                bucType = journalpostSedKobling.bucType,
                avsenderID = organisation.id,
                landkode = organisation.countryCode,
                journalpostID = journalpostSedKobling.journalpostID,
                gsakSaksnummer = gsakSaksnummer?.toString(),
                sedErEndring = journalpostSedKobling.sedVersjon.toInt() != 1,
                sedVersjon = journalpostSedKobling.sedVersjon
            )
        ) ?: throw IllegalStateException("Kunne ikke opprette melding for journalpostSedKobling: $journalpostSedKobling")
    }

    private fun finnVedRinaSaksnummer(journalpostSedKobling: JournalpostSedKobling): FagsakRinasakKobling? =
        saksrelasjonService.finnVedRinaSaksnummer(journalpostSedKobling.rinaSaksnummer).getOrNull()

    private fun opprettEessiMelding(rinaSaksnummer: String, journalpostID: String): MelosysEessiMelding? {
        val buc = euxService.hentBuc(rinaSaksnummer)
        val sedDocument = buc.hentSistOppdaterteDocument() ?: run {
            log.warn("Finner ikke sist oppdaterte sed for rinasak {}", rinaSaksnummer)
            return null
        }
        val sedID = sedDocument.id ?: throw IllegalStateException("sedDocument er null")
        val sedType = sedDocument.type
        val organisation = sedDocument.creator?.organisation ?: throw IllegalStateException("Organisation er null")
        val sed = hentSed(rinaSaksnummer, sedID)
        return opprettMelosysEessiMelding(
            EessiMeldingQuery(
                sed = sed,
                rinaDokumentID = sedID,
                rinaSaksnummer = rinaSaksnummer,
                sedType = sedType,
                bucType = buc.bucType,
                avsenderID = organisation.id,
                landkode = organisation.countryCode,
                journalpostID = journalpostID,
                sedErEndring = false,
                sedVersjon = "0" // har ikke sed-versjon
            )
        ) ?: throw IllegalStateException("Kunne ikke opprette melding for rinasak: $rinaSaksnummer")
    }

    private fun hentSed(rinaSaksnummer: String, dokumentId: String) =
        euxService.hentSed(rinaSaksnummer, dokumentId)
            ?: throw NotFoundException("Fant ikke SED med id $dokumentId i rinasak $rinaSaksnummer")

    private fun opprettMelosysEessiMelding(eessiMeldingQuery: EessiMeldingQuery): MelosysEessiMelding? =
        eessiMeldingQuery.sedType?.let { sedType ->
            melosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedType)).map(eessiMeldingQuery)
        } ?: log.warn("SedType er null for rinasak: ${eessiMeldingQuery.rinaSaksnummer}").run { null }
}
