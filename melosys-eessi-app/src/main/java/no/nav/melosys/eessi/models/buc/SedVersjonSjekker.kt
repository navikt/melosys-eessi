package no.nav.melosys.eessi.models.buc

import mu.KotlinLogging
import no.nav.melosys.eessi.models.sed.SED
import java.util.regex.Pattern
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_G_VER
import no.nav.melosys.eessi.models.sed.Konstanter.DEFAULT_SED_VER

private val log = KotlinLogging.logger {}

object SedVersjonSjekker {
    private val VERSJON_PATTERN = Pattern.compile("^v(\\d)\\.(\\d)$")

    @JvmStatic // TODO: fjern når filer som bruker er konvertert til Kotlin
    fun verifiserSedVersjonErBucVersjon(buc: BUC, sed: SED) {
        val ønsketSedVersjon = "v${sed.sedGVer}.${sed.sedVer}"
        if (!ønsketSedVersjon.equals(buc.bucVersjon, ignoreCase = true)) {
            log.info("Rina-sak {} er på gammel versjon {}. Oppdaterer SED til å bruke gammel versjon", buc.id, buc.bucVersjon)
            sed.sedGVer = parseGVer(buc)
            sed.sedVer = parseVer(buc)
        }
    }

    @JvmStatic // TODO: fjern når filer som bruker er konvertert til Kotlin
    fun parseGVer(buc: BUC): String {
        val matcher = VERSJON_PATTERN.matcher(buc.bucVersjon!!)
        return if (matcher.find()) matcher.group(1) else DEFAULT_SED_G_VER
    }

    @JvmStatic // TODO: fjern når filer som bruker er konvertert til Kotlin
    fun parseVer(buc: BUC): String {
        val matcher = VERSJON_PATTERN.matcher(buc.bucVersjon!!)
        return if (matcher.find()) matcher.group(2) else DEFAULT_SED_VER
    }
}
