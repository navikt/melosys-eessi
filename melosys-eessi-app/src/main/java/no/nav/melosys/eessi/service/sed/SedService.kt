package no.nav.melosys.eessi.service.sed

import io.getunleash.Unleash
import no.nav.melosys.eessi.config.featuretoggle.ToggleName
import no.nav.melosys.eessi.controller.dto.BucOgSedOpprettetDto
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.integration.eux.rina_api.dto.SedJournalstatus
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.FagsakRinasakKobling
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.SedVedlegg
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.SedVersjonSjekker.verifiserSedVersjonErBucVersjon
import no.nav.melosys.eessi.models.exception.IntegrationException
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.exception.ValidationException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import no.nav.melosys.eessi.service.sed.helpers.SedMapperFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class SedService(
    private val euxService: EuxService,
    private val saksrelasjonService: SaksrelasjonService,
    private val unleash: Unleash
) {
    private val log = LoggerFactory.getLogger(SedService::class.java)

    fun opprettBucOgSed(
        sedDataDto: SedDataDto,
        vedlegg: Collection<SedVedlegg>,
        bucType: BucType?,
        sendAutomatisk: Boolean,
        forsøkOppdaterEksisterende: Boolean
    ): BucOgSedOpprettetDto {
        val gsakSaksnummer = hentGsakSaksnummer(sedDataDto)
        log.info("Oppretter buc og sed, gsakSaksnummer: {}", gsakSaksnummer)
        val mottakere = sedDataDto.mottakerIder
        val sedType = bucType!!.hentFørsteLovligeSed()
        val sedMapper = SedMapperFactory.sedMapper(sedType)
        val sed = sedMapper.mapTilSed(sedDataDto, unleash.isEnabled(ToggleName.CDM_4_3))
        validerMottakerInstitusjoner(bucType, mottakere)
        val response = opprettEllerOppdaterBucOgSed(sed, vedlegg, bucType, gsakSaksnummer, sedDataDto.mottakerIder, forsøkOppdaterEksisterende)
        if (sedDataDto.bruker.isHarSensitiveOpplysninger) {
            euxService.settSakSensitiv(response.rinaSaksnummer!!)
        }
        if (sedType.name.startsWith("H")) {
            euxService.settSedJournalstatus(
                response.rinaSaksnummer!!,
                tilUUIDMedBindestreker(response.dokumentId!!),
                0,
                SedJournalstatus.MELOSYS_JOURNALFOERER
            )
        }
        if (sendAutomatisk) {
            sendSed(response.rinaSaksnummer!!, response.dokumentId!!, sed.sedType!!)
        }
        return BucOgSedOpprettetDto.builder()
            .rinaSaksnummer(response.rinaSaksnummer)
            .rinaUrl(euxService.hentRinaUrl(response.rinaSaksnummer))
            .build()
    }

    private fun tilUUIDMedBindestreker(uuidString: String): String {
        return UUID.fromString(
            uuidString.substring(0, 8) + "-" +
                uuidString.substring(8, 12) + "-" +
                uuidString.substring(12, 16) + "-" +
                uuidString.substring(16, 20) + "-" +
                uuidString.substring(20)
        ).toString()
    }

    @Throws(ValidationException::class)
    private fun validerMottakerInstitusjoner(bucType: BucType, mottakere: Collection<String>) {
        if (mottakere.isEmpty()) {
            throw ValidationException("Mottakere er påkrevd")
        } else if (!bucType.erMultilateralLovvalgBuc() && mottakere.size > 1) {
            throw ValidationException("$bucType kan ikke ha flere mottakere!")
        }
    }

    private fun sendSed(rinaSaksnummer: String, dokumentId: String, sedType: String) {
        try {
            TimeUnit.SECONDS.sleep(10L)
            euxService.sendSed(rinaSaksnummer, dokumentId, sedType)
        } catch (e: IntegrationException) {
            log.error("Feil ved oppretting og/eller sending av buc og sed. Exception fanges for å slette saksrelasjon.")
            slettBucOgSaksrelasjon(rinaSaksnummer)
            throw e
        } catch (e: InterruptedException) {
            log.error("Uventet InterruptedException", e)
            Thread.currentThread().interrupt()
            slettBucOgSaksrelasjon(rinaSaksnummer)
            throw RuntimeException(e)
        }
    }

    private fun slettBucOgSaksrelasjon(rinaSaksnummer: String) {
        euxService.slettBUC(rinaSaksnummer)
        saksrelasjonService.slettVedRinaId(rinaSaksnummer)
    }

    fun genererPdfFraSed(sedDataDto: SedDataDto, sedType: SedType): ByteArray? {
        val sedMapper = SedMapperFactory.sedMapper(sedType)
        val sed = sedMapper.mapTilSed(sedDataDto, unleash.isEnabled(ToggleName.CDM_4_3))
        return euxService.genererPdfFraSed(sed)
    }

    fun sendPåEksisterendeBuc(sedDataDto: SedDataDto, rinaSaksnummer: String, sedType: SedType) {
        val buc = euxService.hentBuc(rinaSaksnummer)
        val sed = SedMapperFactory.sedMapper(sedType).mapTilSed(sedDataDto, unleash.isEnabled(ToggleName.CDM_4_3))
        verifiserSedVersjonErBucVersjon(buc, sed)
        euxService.opprettOgSendSed(sed, rinaSaksnummer)
    }

    private fun opprettEllerOppdaterBucOgSed(
        sed: SED,
        vedlegg: Collection<SedVedlegg>,
        bucType: BucType,
        gsakSaksnummer: Long,
        mottakerIder: List<String>,
        forsøkOppdaterEksisterende: Boolean
    ): OpprettBucOgSedResponse {
        if (forsøkOppdaterEksisterende && bucType.meddelerLovvalg()) {
            val eksisterendeSak = finnAapenEksisterendeSak(saksrelasjonService.finnVedGsakSaksnummerOgBucType(gsakSaksnummer, bucType))
            if (eksisterendeSak.isPresent && eksisterendeSak.get().erÅpen()) {
                val buc = eksisterendeSak.get()
                val document = buc.finnDokumentVedSedType(sed.sedType!!)
                if (document != null && buc.sedKanOppdateres(document.id!!)) {
                    val rinaSaksnummer = buc.id
                    val dokumentId = document.id
                    verifiserSedVersjonErBucVersjon(buc, sed)
                    log.info("SED {} på rinasak {} oppdateres", dokumentId, rinaSaksnummer)
                    euxService.oppdaterSed(rinaSaksnummer, dokumentId, sed)
                    return OpprettBucOgSedResponse(rinaSaksnummer, dokumentId)
                }
            }
        }
        return opprettOgLagreSaksrelasjon(sed, vedlegg, bucType, gsakSaksnummer, mottakerIder)
    }

    private fun finnAapenEksisterendeSak(eksisterendeSaker: List<FagsakRinasakKobling>): Optional<BUC> {
        for (fagsakRinasakKobling in eksisterendeSaker) {
            val buc = euxService.finnBUC(fagsakRinasakKobling.rinaSaksnummer)
            if (buc.isPresent && buc.get().erÅpen()) {
                return buc
            }
        }
        return Optional.empty()
    }

    private fun opprettOgLagreSaksrelasjon(
        sed: SED,
        vedlegg: Collection<SedVedlegg>,
        bucType: BucType,
        gsakSaksnummer: Long,
        mottakerIder: List<String>
    ): OpprettBucOgSedResponse {
        val opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakerIder, sed, vedlegg)
        saksrelasjonService.lagreKobling(gsakSaksnummer, opprettBucOgSedResponse.rinaSaksnummer, bucType)
        log.info("gsakSaksnummer {} lagret med rinaId {}", gsakSaksnummer, opprettBucOgSedResponse.rinaSaksnummer)
        return opprettBucOgSedResponse
    }

    private fun hentGsakSaksnummer(sedDataDto: SedDataDto): Long {
        return sedDataDto.gsakSaksnummer ?: throw MappingException("GsakId er påkrevd!")
    }
}
