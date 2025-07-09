package no.nav.melosys.eessi.service.mottak

import no.nav.melosys.eessi.service.sed.helpers.EøsLandkoder

object SedA003UnntaksreglerForTredjelandsborgere {
    // https://jira.adeo.no/browse/MELOSYS-7403
    private val NORDISK_AVTALELAND_UNNTATT_TREDJELANDSBORGER_SJEKK = setOf(
        EøsLandkoder.NL,
        EøsLandkoder.AT,
        EøsLandkoder.LU,
        EøsLandkoder.GB,
        EøsLandkoder.CH,
    )

    fun avsenderErFraGodkjentLandForUnntak(avsenderLandkode: String?): Boolean {
        val eøsAvsenderLand = EøsLandkoder.entries.firstOrNull { it.name == avsenderLandkode }
        return eøsAvsenderLand in NORDISK_AVTALELAND_UNNTATT_TREDJELANDSBORGER_SJEKK
    }
}
