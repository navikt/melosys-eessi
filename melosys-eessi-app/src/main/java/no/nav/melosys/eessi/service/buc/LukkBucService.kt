package no.nav.melosys.eessi.service.buc

import io.getunleash.Unleash
import mu.KotlinLogging
import no.nav.melosys.eessi.metrikker.BucMetrikker
import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.buc.BUC
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.buc.SedVersjonSjekker.verifiserSedVersjonErBucVersjon
import no.nav.melosys.eessi.models.exception.IntegrationException
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.eux.BucSearch
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.sed.mapper.til_sed.administrativ.X001Mapper
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class LukkBucService(
    private val euxService: EuxService,
    private val bucMetrikker: BucMetrikker,
    unleash: Unleash,
) {
    private val x001Mapper = X001Mapper(unleash)

    fun lukkBucerAvType(bucType: BucType) {
        try {
            log.info("Lukker bucer av type {}", bucType)
            // FIXME: søk på BUC fungerer ikke med status open. Venter på eux/rina-fix
            val bucInfos = euxService.hentBucer(BucSearch(bucType = bucType.name))
            bucInfos
                .filter { it.bucErÅpen() && it.norgeErCaseOwner() }
                .mapNotNull { euxService.finnBUC(it.id).orElse(null) }
                .filter { it.kanLukkesAutomatisk() }
                .forEach { lukkBuc(it) }
        } catch (e: IntegrationException) {
            log.error("Feil ved henting av bucer av type {}", bucType, e)
        }
    }

    /*
    Async for at ekstern tjeneste ikke skal trenge å vente på resultat herfra.
    Blir kalt eksternt for å indikere at en tilhørende behandling er avsluttet, og at man kan anse utveksling som ferdig.
    Kan fortsatt ikke garantere at RINA har tilgjengeliggjort lukking av BUCen (create X001)
    */
    fun forsøkLukkBucAsync(rinaSaksnummer: String) {
        try {
            val buc = euxService.finnBUC(rinaSaksnummer)
            buc.filter { it.kanOppretteEllerOppdatereSed(SedType.X001) }
                .ifPresentOrElse(
                    { lukkBuc(it) },
                    { log.info("Ikke mulig å opprette X001 i rina-sak {}", rinaSaksnummer) }
                )
        } catch (e: Exception) {
            log.warn("Feil ved forsøk av lukking av BUC {}", rinaSaksnummer)
        }
    }

    private fun lukkBuc(buc: BUC) {
        try {
            val x001 = opprettX001(buc, LukkBucAarsakMapper.hentAarsakForLukking(buc))
            verifiserSedVersjonErBucVersjon(buc, x001)
            val eksisterendeX001 = finnEksisterendeX001Utkast(buc)
            if (eksisterendeX001 != null) {
                euxService.oppdaterSed(buc.id, eksisterendeX001.id, x001)
                euxService.sendSed(buc.id, eksisterendeX001.id, x001.sedType)
            } else {
                euxService.opprettOgSendSed(x001, buc.id)
            }
            bucMetrikker.bucLukket(buc.bucType)
            log.info("BUC {} lukket med årsak {}", buc.id, x001.nav?.sak?.anmodning?.avslutning?.aarsak?.type)
        } catch (e: IntegrationException) {
            log.error("Kunne ikke lukke buc {}", buc.id, e)
        }
    }

    private fun finnEksisterendeX001Utkast(buc: BUC): Document? =
        buc.documents
            .filter { it.erX001() && it.erOpprettet() }
            .minByOrNull { it.creationDate!! }

    private fun opprettX001(buc: BUC, aarsak: String): SED =
        x001Mapper.mapFraSed(
            hentSisteLovvalgSed(buc),
            aarsak
        )

    private fun hentSisteLovvalgSed(buc: BUC): SED =
        buc.documents
            .filter { it.sedErSendt() }
            .minByOrNull { it.creationDate!! }
            ?.let { euxService.hentSed(buc.id, it.id) }
            ?: throw IllegalStateException("Finner ingen lovvalgs-SED på buc ${buc.id}")
}
