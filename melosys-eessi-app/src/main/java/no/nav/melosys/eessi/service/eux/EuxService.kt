package no.nav.melosys.eessi.service.eux

import mu.KotlinLogging
import no.nav.melosys.eessi.integration.eux.rina_api.Aksjoner
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer
import no.nav.melosys.eessi.integration.eux.rina_api.EuxRinasakerConsumer
import no.nav.melosys.eessi.integration.eux.rina_api.dto.EuxMelosysSedOppdateringDto
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon
import no.nav.melosys.eessi.integration.eux.rina_api.dto.SedJournalstatus
import no.nav.melosys.eessi.metrikker.BucMetrikker
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedVedlegg
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.bucinfo.BucInfo
import no.nav.melosys.eessi.models.exception.IntegrationException
import no.nav.melosys.eessi.models.exception.NotFoundException
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg
import no.nav.melosys.eessi.service.sed.LandkodeMapper
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class EuxService(
    private val euxConsumer: EuxConsumer,
    private val bucMetrikker: BucMetrikker,
    private val euxRinasakerConsumer: EuxRinasakerConsumer
) {

    fun slettBUC(rinaSaksnummer: String) {
        euxConsumer.slettBUC(rinaSaksnummer)
    }

    fun opprettBucOgSed(
        bucType: BucType,
        mottakere: Collection<String>,
        sed: SED,
        vedlegg: Collection<SedVedlegg>
    ): OpprettBucOgSedResponse {
        val rinaSaksnummer = euxConsumer.opprettBUC(bucType.name)
        euxConsumer.settMottakere(rinaSaksnummer, mottakere)
        val dokumentID = euxConsumer.opprettSed(rinaSaksnummer, sed)
        vedlegg.forEach { leggTilVedlegg(rinaSaksnummer, dokumentID, it) }
        bucMetrikker.bucOpprettet(bucType.name)
        log.info("Buc opprettet med id: {} og sed opprettet med id: {}", rinaSaksnummer, dokumentID)
        return OpprettBucOgSedResponse(rinaSaksnummer, dokumentID)
    }

    fun sendSed(rinaSaksnummer: String?, dokumentId: String?, sedType: String?) {
        validerSedHandling(rinaSaksnummer, dokumentId, Aksjoner.SEND)
        euxConsumer.sendSed(rinaSaksnummer, dokumentId)
        log.info("SED $sedType sendt i sak $rinaSaksnummer")
    }

    fun oppdaterSed(rinaSaksnummer: String?, dokumentId: String?, sed: SED) {
        euxConsumer.oppdaterSed(rinaSaksnummer, dokumentId, sed)
    }

    fun hentMottakerinstitusjoner(bucType: String, landkoder: Collection<String>): List<Institusjon> =
        euxConsumer.hentInstitusjoner(bucType, null)
            .onEach { it.landkode = LandkodeMapper.mapTilNavLandkode(it.landkode) }
            .filter { filtrerPåLandkoder(it, landkoder) }
            .filter { institusjon ->
                institusjon.tilegnetBucs.orEmpty().any { tilegnetBuc ->
                    bucType == tilegnetBuc.bucType && COUNTERPARTY == tilegnetBuc.institusjonsrolle && tilegnetBuc.erEessiKlar()
                }
            }

    fun opprettOgSendSed(sed: SED?, rinaSaksnummer: String?) {
        validerBucHandling(rinaSaksnummer, Aksjoner.CREATE)
        val sedId = euxConsumer.opprettSed(rinaSaksnummer, sed)
        validerSedHandling(rinaSaksnummer, sedId, Aksjoner.SEND)
        euxConsumer.sendSed(rinaSaksnummer, sedId)
        log.info("SED ${sed?.sedType} opprett og sendt i sak {rinaSaksnummer}")
    }


    fun sedErEndring(sedId: String, rinaSaksnummer: String): Boolean {
        val buc = euxConsumer.hentBUC(rinaSaksnummer)
        return buc.documents.any { it.id == sedId && it.conversations.size > 1 }
    }

    fun hentSed(rinaSaksnummer: String?, dokumentId: String?): SED? = euxConsumer.hentSed(rinaSaksnummer, dokumentId)

    @Retryable
    fun hentSedMedRetry(rinaSaksnummer: String, dokumentId: String): SED =
        hentSed(rinaSaksnummer, dokumentId)
            ?: throw NotFoundException("Fant ikke SED med id $dokumentId i rinasak $rinaSaksnummer")

    fun hentBucer(bucSearch: BucSearch): List<BucInfo> = euxConsumer.finnRinaSaker(bucSearch.bucType, bucSearch.status)

    fun hentBuc(rinaSaksnummer: String): BUC = euxConsumer.hentBUC(rinaSaksnummer)

    fun finnBUC(rinaSaksnummer: String?): Optional<BUC> = try {
        Optional.of(euxConsumer.hentBUC(rinaSaksnummer))
    } catch (e: IntegrationException) {
        log.warn("Kan ikke hente BUC {}", rinaSaksnummer, e)
        Optional.empty()
    } catch (e: NotFoundException) {
        log.warn("Kan ikke hente BUC {}", rinaSaksnummer, e)
        Optional.empty()
    }

    fun hentSedMedVedlegg(rinaSaksnummer: String, dokumentId: String): SedMedVedlegg =
        euxConsumer.hentSedMedVedlegg(rinaSaksnummer, dokumentId)

    fun genererPdfFraSed(sed: SED): ByteArray? = euxConsumer.genererPdfFraSed(sed)

    fun hentRinaUrl(rinaCaseId: String?): String? {
        require(StringUtils.hasText(rinaCaseId)) { "Trenger rina-saksnummer for å opprette url til rina" }
        return euxConsumer.hentRinaUrl(rinaCaseId)
    }

    fun settSedJournalstatus(rinaSaksnummer: String, dokumentId: String, versjon: Int, sedJournalstatus: SedJournalstatus) {
        require(StringUtils.hasText(rinaSaksnummer)) { "Trenger rina-saksnummer for å oppdatere sed" }
        euxRinasakerConsumer.settSedJournalstatus(EuxMelosysSedOppdateringDto(rinaSaksnummer, dokumentId, versjon, sedJournalstatus))
    }

    fun settSakSensitiv(rinaSaksnummer: String) = euxConsumer.setSakSensitiv(rinaSaksnummer)

    private fun validerBucHandling(rinaSaksnummer: String?, aksjon: Aksjoner) {
        if (!bucHandlingErMulig(rinaSaksnummer, aksjon)) {
            throw ValidationException("Kan ikke gjøre handling ${aksjon.hentHandling()} på BUC $rinaSaksnummer, ugyldig handling i Rina")
        }
    }

    private fun leggTilVedlegg(rinaSaksnummer: String, dokumentID: String, vedlegg: SedVedlegg) {
        val vedleggID = euxConsumer.leggTilVedlegg(rinaSaksnummer, dokumentID, FILTYPE_PDF, vedlegg)
        log.info("Lagt til vedlegg med ID {} i rinasak {}", vedleggID, rinaSaksnummer)
    }

    private fun filtrerPåLandkoder(institusjon: Institusjon, landkoder: Collection<String>): Boolean =
        landkoder.isEmpty() || landkoder.any { it.equals(institusjon.landkode, ignoreCase = true) }

    private fun validerSedHandling(rinaSaksnummer: String?, sedId: String?, aksjon: Aksjoner) {
        if (!sedHandlingErMulig(rinaSaksnummer, sedId, aksjon)) {
            throw ValidationException("Kan ikke sende SED på BUC $rinaSaksnummer, ugyldig handling ${aksjon.hentHandling()} i Rina")
        }
    }

    private fun bucHandlingErMulig(rinaSaksnummer: String?, aksjon: Aksjoner): Boolean =
        euxConsumer.hentBucHandlinger(rinaSaksnummer).any { it.split(" ")[2] == aksjon.hentHandling() }

    private fun sedHandlingErMulig(rinaSaksnummer: String?, dokumentId: String?, handling: Aksjoner): Boolean =
        euxConsumer.hentSedHandlinger(rinaSaksnummer, dokumentId).any { it == handling.hentHandling() }

    companion object {
        private const val COUNTERPARTY = "CounterParty"
        private const val FILTYPE_PDF = "pdf"
    }
}
