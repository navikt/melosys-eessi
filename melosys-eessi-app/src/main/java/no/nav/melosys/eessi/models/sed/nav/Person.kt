package no.nav.melosys.eessi.models.sed.nav

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.melosys.eessi.identifisering.FnrUtils
import java.util.*
import kotlin.jvm.optionals.getOrNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Person(
    var etternavn: String? = null,
    var etternavnvedfoedsel: String? = null,
    var foedested: Foedested? = null,
    var foedselsdato: String? = null,
    var fornavn: String? = null,
    var fornavnvedfoedsel: String? = null,
    var kjoenn: Kjønn? = null,
    var pin: List<Pin> = emptyList(),
    var statsborgerskap: List<Statsborgerskap?> = emptyList()
) {
    fun hentStatsborgerksapsliste(): Collection<String> = statsborgerskap.mapNotNull { it?.land }

    fun harStatsborgerskap(land: String): Boolean = hentStatsborgerksapsliste().contains(land)

    fun finnUtenlandskIdFraLand(land: String): Optional<Pin> = pin.firstOrNull { it.land == land }?.let { Optional.of(it) } ?: Optional.empty()

    fun hentNorskPersonnummer(): String? =
        pin.firstOrNull { it.land == "NO" }?.identifikator?.let { FnrUtils.filtrerUtGyldigNorskIdent(it).getOrNull() }

    fun harNorskPersonnummer(): Boolean = hentNorskPersonnummer() != null
}
