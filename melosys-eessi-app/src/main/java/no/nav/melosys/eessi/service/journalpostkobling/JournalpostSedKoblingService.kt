package no.nav.melosys.eessi.service.journalpostkobling

import no.nav.melosys.eessi.integration.saf.SafConsumer
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import no.nav.melosys.eessi.models.JournalpostSedKobling
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class JournalpostSedKoblingService(
    private val journalpostSedKoblingRepository: JournalpostSedKoblingRepository,
    private val euxService: EuxService,
    private val saksrelasjonService: SaksrelasjonService,
    private val safConsumer: SafConsumer,
    private val melosysEessiMeldingMapperFactory: MelosysEessiMeldingMapperFactory
) {
    fun finnVedJournalpostID(journalpostID: String): Optional<JournalpostSedKobling> {
        return journalpostSedKoblingRepository.findByJournalpostID(journalpostID)
    }

    fun finnVedJournalpostIDOpprettMelosysEessiMelding(journalpostID: String): Optional<MelosysEessiMelding> {
        val journalpostSedKobling = journalpostSedKoblingRepository.findByJournalpostID(journalpostID)
        if (journalpostSedKobling.isPresent) {
            return Optional.of(opprettEessiMelding(journalpostSedKobling.get()))
        }
        val rinaSaksnummer = søkEtterRinaSaksnummerForJournalpost(journalpostID)
        if (rinaSaksnummer.isPresent) {
            return opprettEessiMelding(rinaSaksnummer.get(), journalpostID)
        }
        return Optional.empty()
    }

    fun erASedAlleredeBehandlet(rinaSaksnummer: String): Boolean {
        return journalpostSedKoblingRepository.findByRinaSaksnummer(rinaSaksnummer).stream().anyMatch { obj: JournalpostSedKobling -> obj.erASed() }
    }

    private fun søkEtterRinaSaksnummerForJournalpost(journalpostID: String): Optional<String> {
        val rinaSaksnummer = safConsumer.hentRinasakForJournalpost(journalpostID)
        if (rinaSaksnummer.isEmpty) {
            log.info("Rinasaksnummer er null fra saf for journalpostId: {}", journalpostID)
        }
        return rinaSaksnummer
    }

    private fun opprettEessiMelding(journalpostSedKobling: JournalpostSedKobling): MelosysEessiMelding {
        val buc = euxService.hentBuc(journalpostSedKobling.rinaSaksnummer)
        val organisation = buc.hentDokument(journalpostSedKobling.sedId).creator!!.organisation
        val sed = euxService.hentSed(journalpostSedKobling.rinaSaksnummer, journalpostSedKobling.sedId)
        val gsakSaksnummer =
            saksrelasjonService.finnVedRinaSaksnummer(journalpostSedKobling.rinaSaksnummer).map(FagsakRinasakKobling::gsakSaksnummer).orElse(null)
        return opprettMelosysEessiMelding(
            sed!!,
            journalpostSedKobling.sedId,
            journalpostSedKobling.rinaSaksnummer,
            journalpostSedKobling.sedType,
            journalpostSedKobling.bucType,
            organisation!!.id,
            organisation.countryCode,
            journalpostSedKobling.journalpostID,
            gsakSaksnummer?.toString(),
            journalpostSedKobling.sedVersjon.toInt() != 1,
            journalpostSedKobling.sedVersjon
        )
    }

    private fun opprettEessiMelding(rinaSaksnummer: String, journalpostID: String): Optional<MelosysEessiMelding> {
        val buc = euxService.hentBuc(rinaSaksnummer)
        val documentOptional = buc.hentSistOppdaterteDocument()
        if (documentOptional == null) {
            log.warn("Finner ikke sist oppdaterte sed for rinasak {}", rinaSaksnummer)
            return Optional.empty()
        }
        val sedDocument: Document = documentOptional
        val sedID = sedDocument.id
        val sedType = sedDocument.type
        val organisation = sedDocument.creator!!.organisation
        val sed = euxService.hentSed(rinaSaksnummer, sedID)
        val sedVersjon = "0" //har ikke sed-versjon
        return Optional.of(
            opprettMelosysEessiMelding(
                sed!!,
                sedID,
                rinaSaksnummer,
                sedType,
                buc.bucType,
                organisation!!.id,
                organisation.countryCode,
                journalpostID,
                null,
                false,
                sedVersjon
            )
        )
    }

    fun lagre(
        journalpostID: String,
        rinaSaksnummer: String,
        sedID: String,
        sedVersjon: String,
        bucType: String,
        sedType: String
    ): JournalpostSedKobling {
        return journalpostSedKoblingRepository.save(JournalpostSedKobling(journalpostID, rinaSaksnummer, sedID, sedVersjon, bucType, sedType))
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
    ): MelosysEessiMelding {
        return melosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedType!!))
            .map(null, sed, sedId, rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, null, saksnummer, erEndring, sedVersjon)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(JournalpostSedKoblingService::class.java)
    }
}
