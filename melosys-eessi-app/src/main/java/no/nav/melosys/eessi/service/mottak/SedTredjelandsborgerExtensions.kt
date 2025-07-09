package no.nav.melosys.eessi.service.mottak

import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.service.sed.helpers.EøsLandkoder
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper
import kotlin.jvm.optionals.getOrNull

fun SED.erNorgeNevntSomArbeidsSted(land: String = EøsLandkoder.NO.value.lowercase()): Boolean =
    this.nav?.arbeidssted?.any { it.adresse?.land?.lowercase() == land } ?: false
        || this.nav?.arbeidsland?.any { it.land == land } ?: false

/**
 * Det skal ikke blir rekvirert d-nummer på bakgrunn av mottatt A003, når den gjelder en tredjelandsborger og arbeidssted ikke er Norge
 */
fun SED.sedErA003OgTredjelandsborgerUtenNorgeSomArbeidssted(hentAvsenderLand: () -> String): Boolean {
    if (sedType != SedType.A003.name) return false // Ikke A003
    if ((medlemskap as MedlemskapA003).vedtak?.land == "NO") return false // Norge er lovvalgsland

    val person = finnPerson().getOrNull() ?: return false // Personen finnes ikke hos NAV
    if (person.harNorskPersonnummer()) {
        // personen det gjelder har norsk fnr eller d-nr
        return false
    }

    if (person.statsborgerskap.any { LandkodeMapper.erEøsLand(it?.land) }) {
        // personen det gjelder er EØS-borger, så ikke en tredjelandsborger
        return false
    }

    if (erNorgeNevntSomArbeidsSted()) return false// Norge er nevnt som arbeidssted

    if (UfmRegler.erTredjelandsborgerIkkeAvtaleland(hentAvsenderLand(), person.statsborgerskap.mapNotNull { it?.land }))
        return false // Sjekk: Avsender er tredjelandsborger uten arbeidssted i Norge

    return true
}
