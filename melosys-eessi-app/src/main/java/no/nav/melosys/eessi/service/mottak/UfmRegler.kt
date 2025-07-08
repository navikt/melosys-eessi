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
    )

    fun statsborgerskapErMedlemsland(statsborgerskapLandkoder: Collection<String>) =
        EøsLandkoder.entries.any { it.name in statsborgerskapLandkoder }

    fun avsenderErNordiskEllerAvtaleland(avsenderLandkode: EøsLandkoder?) =
        avsenderLandkode in NORDISK_ELLER_AVTALELAND

    fun erStatsløs(statsborgerskapLandkoder: Collection<String>) =
        statsborgerskapLandkoder.isNotEmpty() && LandkodeMapper.STATSLØS_LANDKODE_ISO2 in statsborgerskapLandkoder

    fun lovvalgslandErNorge(landkode: EøsLandkoder) =
        landkode == EøsLandkoder.NO
}
