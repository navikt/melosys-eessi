package no.nav.melosys.eessi.kafka.producers.model

data class Adresse(
    var by: String?,
    var bygning: String?,
    var gate: String?,
    var land: String?,
    var postnummer: String?,
    var region: String?,
    var type: String?
) {
    constructor(adresse: no.nav.melosys.eessi.models.sed.nav.Adresse) : this(
        adresse.by,
        adresse.bygning,
        adresse.gate,
        adresse.land,
        adresse.postnummer,
        adresse.region,
        adresse.type
    )
}
