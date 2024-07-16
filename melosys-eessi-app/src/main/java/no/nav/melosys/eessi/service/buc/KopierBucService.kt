package no.nav.melosys.eessi.service.buc

import no.nav.melosys.eessi.models.BucType
import no.nav.melosys.eessi.models.buc.Document
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.eux.EuxService
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService
import org.springframework.stereotype.Service

import kotlin.NoSuchElementException
import kotlin.jvm.optionals.getOrNull

@Service
class KopierBucService(
    private val euxService: EuxService,
    private val saksrelasjonService: SaksrelasjonService
) {

    fun kopierBUC(rinaSaksnummer: String): String {
        val buc = euxService.hentBuc(rinaSaksnummer)
        val bucType = BucType.valueOf(buc.bucType!!)
        val førsteSEDType = bucType.hentFørsteLovligeSed()

        val sed = buc.documents
            .filter { førsteSEDType.name == it.type }
            .filter(Document::erOpprettet)
            .minByOrNull { it.creationDate!! }
            ?.let { euxService.hentSed(rinaSaksnummer, it.id) }
            ?: throw NoSuchElementException("Finner ikke første SED for rinasak $rinaSaksnummer")

        settYtterligereInfo(sed, buc.internationalId!!)
        val nyttRinaSaksnummer = euxService.opprettBucOgSed(bucType, buc.hentMottakere(), sed, emptySet()).rinaSaksnummer

        saksrelasjonService.finnVedRinaSaksnummer(rinaSaksnummer).getOrNull()
            ?.let { saksrelasjonService.lagreKobling(it.gsakSaksnummer, nyttRinaSaksnummer, bucType) }

        return nyttRinaSaksnummer
    }

    private fun settYtterligereInfo(sed: SED, internasjonalID: String) {
        val infoTekst = hentInfoTekst(sed.sedType!!, internasjonalID)
        val ytterligereInfo = sed.nav!!.ytterligereinformasjon.orEmpty()

        sed.nav!!.ytterligereinformasjon = if ((ytterligereInfo.length + infoTekst.length) > MAKS_LENGDE_YTTERLIGERE_INFO) {
            infoTekst
        } else {
            "$ytterligereInfo\n\n$infoTekst"
        }
    }

    private fun hentInfoTekst(sedType: String, internasjonalID: String): String = """
        Due to an error in Rina, we are sending you a new $sedType.
        This BUC replaces a previously sent BUC with International ID: $internasjonalID.
        We are unable to read your reply to our $sedType in the original BUC.
        Please reply in this BUC. We apologize for any inconvenience this may have caused.
    """.trimIndent()

    companion object {
        private const val MAKS_LENGDE_YTTERLIGERE_INFO = 498 //500 minus to "\n"
    }
}
