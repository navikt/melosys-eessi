package no.nav.melosys.eessi.service.mottak

import no.nav.melosys.eessi.service.sed.helpers.EøsLandkoder
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper

object UfmRegler {
    private val NORDISK_ELLER_AVTALELAND = setOf(
        EøsLandkoder.SE,
        EøsLandkoder.DK,
        EøsLandkoder.FI,
        EøsLandkoder.IS,
        EøsLandkoder.AT,
        EøsLandkoder.NL,
        EøsLandkoder.LU
        // Dette er kopiert fra melosys-api
        // Storbritannia og Sveits skal hånters på egen måte - ny oppdatering i https://jira.adeo.no/browse/MELOSYS-7403
    )

    fun statsborgerskapErMedlemsland(statsborgerskapLandkoder: List<String>) =
        EøsLandkoder.entries.any { it.name in statsborgerskapLandkoder }

    fun avsenderErNordiskEllerAvtaleland(avsenderLandkode: String?): Boolean {
        val eøsAvsenderLand = EøsLandkoder.entries.firstOrNull { it.name == avsenderLandkode }
        return eøsAvsenderLand in NORDISK_ELLER_AVTALELAND
    }

    fun erStatsløs(statsborgerskapLandkoder: List<String>) =
        LandkodeMapper.STATSLØS_LANDKODE_ISO2 in statsborgerskapLandkoder

    fun erTredjelandsborgerIkkeAvtaleland(avsenderLandkode: String?, statsborgerskapLandkoder: List<String>): Boolean =
        avsenderErNordiskEllerAvtaleland(avsenderLandkode)
            || erStatsløs(statsborgerskapLandkoder)
            || statsborgerskapErMedlemsland(statsborgerskapLandkoder)
}
