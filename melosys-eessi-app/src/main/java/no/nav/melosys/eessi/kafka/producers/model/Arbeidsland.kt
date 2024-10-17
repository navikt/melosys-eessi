package no.nav.melosys.eessi.kafka.producers.model

data class Arbeidsland(
    var land: String?,
    var arbeidssted: List<Arbeidssted>?
) {
    constructor(arbeidsland: no.nav.melosys.eessi.models.sed.nav.Arbeidsland) : this(
        arbeidsland.land,
        arbeidsland.arbeidssted?.map { Arbeidssted(it) }
    )
}
