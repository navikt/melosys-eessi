package no.nav.melosys.eessi.service.mottak

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.service.sed.helpers.EøsLandkoder
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import kotlin.jvm.optionals.getOrNull

fun SED.erNorgeNevntSomArbeidsSted(land: String = EøsLandkoder.NO.name): Boolean =
    this.nav?.arbeidssted?.any { it.adresse?.land == land } ?: false
        || this.nav?.arbeidsland?.any { it.land == land } ?: false

/**
 * Det skal ikke blir rekvirert d-nummer på bakgrunn av mottatt A003, når den gjelder en tredjelandsborger og arbeidssted ikke er Norge
 * https://jira.adeo.no/browse/MELOSYS-7403
 */
fun SED.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted(hentAvsenderLand: () -> String, reason: (a: String) -> Unit = {}): Boolean {
    if (sedType != SedType.A003.name) {
        reason("Ikke A003 SED")
        return false
    }
    if ((medlemskap as MedlemskapA003).vedtak?.land == "NO") {
        reason("Norge er lovvalgsland")
        return false
    }

    val person = finnPerson().getOrNull() ?: run {
        reason("Ingen person funnet")
        return false
    }

    if (person.harNorskPersonnummer()) {
        reason("Person har norsk fnr eller d-nr")
        return false
    }

    if (person.hentStatsborgerksapsliste().any { LandkodeMapper.erEøsLand(it) }) {
        reason("Person er EØS-borger, så ikke en tredjelandsborger")
        return false
    }

    if (erNorgeNevntSomArbeidsSted()) {
        reason("Norge er nevnt som arbeidssted")
        return false
    }

    if (SedA003UnntaksreglerForTredjelandsborgere.avsenderErFraGodkjentLandForUnntak(avsenderLandkode = hentAvsenderLand())) {
        reason("Avsender er fra godkjent land for unntak")
        return false
    }

    return true
}
