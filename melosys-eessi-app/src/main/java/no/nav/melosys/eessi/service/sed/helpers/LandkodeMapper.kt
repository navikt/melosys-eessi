package no.nav.melosys.eessi.service.sed.helpers

import java.util.*

/**
 * Mapper mellom landkoder i ISO2 og ISO3 format.
 *
 *
 * Legg merke til kode for ukjent land der vi bruker XUK for ISO3 og XU for ISO2. Disse kodene er ikke i bruk i ISO-standarden,
 * men er valgt på grunn av at de er i bruk i PDL og Rina. Disse gjenbruker vi i Melosys for å unngå å måtte lage egne.
 */
object LandkodeMapper {
    const val UKJENT_LANDKODE_ISO3 = "XUK"
    const val UKJENT_LANDKODE_ISO2 = "XU"

    const val KOSOVO_LANDKODE_ISO3 = "XXK" // Kosovo - XXK brukes i Nav kodeverk
    const val KOSOVO_LANDKODE_ISO2 = "XK"

    private const val STATSLØS_LANDKODE_ISO3 = "XXX"
    private const val STATSLØS_LANDKODE_ISO2 = "XS"

    private val ISO3_TIL_ISO2_LANDKODER_MAP = Locale.getISOCountries()
        .associateBy { Locale.Builder().setRegion(it).build().isO3Country }.toMap() +
        mapOf(
            STATSLØS_LANDKODE_ISO3 to STATSLØS_LANDKODE_ISO2,
            UKJENT_LANDKODE_ISO3 to UKJENT_LANDKODE_ISO2,
            KOSOVO_LANDKODE_ISO3 to KOSOVO_LANDKODE_ISO2,
        )

    @JvmStatic
    fun mapTilLandkodeIso2(landkodeIso3: String?): String =
        finnLandkodeIso2(landkodeIso3).orElse(UKJENT_LANDKODE_ISO2)

    @JvmStatic
    fun finnLandkodeIso2(landkodeIso3: String?): Optional<String> =
        when {
            landkodeIso3 == null -> Optional.empty()
            landkodeIso3.length == 2 -> Optional.of(landkodeIso3)
            else -> Optional.ofNullable(ISO3_TIL_ISO2_LANDKODER_MAP[landkodeIso3])
        }

    @JvmStatic
    fun finnLandkodeIso3ForIdentRekvisisjon(landkodeIso2: String?, skalReturnereNullForUkjent: Boolean): String? =
        when {
            landkodeIso2 == null -> null
            landkodeIso2.length == 3 -> landkodeIso2
            else -> ISO3_TIL_ISO2_LANDKODER_MAP.entries
                .firstOrNull { it.value == mapTilNavLandkode(landkodeIso2) }
                ?.key ?: if (skalReturnereNullForUkjent) null else UKJENT_LANDKODE_ISO3
        }

    @JvmStatic
    fun mapTilNavLandkode(landkode: String?): String? =
        when (landkode?.uppercase()) {
            "UK" -> "GB"
            "EL" -> "GR"
            else -> landkode
        }
}
